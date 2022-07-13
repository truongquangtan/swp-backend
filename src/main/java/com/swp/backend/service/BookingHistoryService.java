package com.swp.backend.service;

import com.swp.backend.api.v1.booking_history.BookingHistoryResponse;
import com.swp.backend.constance.RoleProperties;
import com.swp.backend.entity.BookingEntity;
import com.swp.backend.entity.BookingHistoryEntity;
import com.swp.backend.model.BookingHistoryModel;
import com.swp.backend.model.FilterModel;
import com.swp.backend.model.MatchModel;
import com.swp.backend.model.SearchModel;
import com.swp.backend.model.model_builder.BookingHistoryEntityBuilder;
import com.swp.backend.myrepository.BookingHistoryCustomRepository;
import com.swp.backend.repository.BookingHistoryRepository;
import com.swp.backend.repository.BookingRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingHistoryService {
    private MatchService matchService;
    private BookingRepository bookingRepository;
    private BookingHistoryRepository bookingHistoryRepository;
    private BookingHistoryCustomRepository bookingHistoryCustomRepository;

    private AccountService accountService;

    public BookingHistoryService(MatchService matchService, BookingRepository bookingRepository, BookingHistoryRepository bookingHistoryRepository, BookingHistoryCustomRepository bookingHistoryCustomRepository, @Lazy AccountService accountService) {
        this.matchService = matchService;
        this.bookingRepository = bookingRepository;
        this.bookingHistoryRepository = bookingHistoryRepository;
        this.bookingHistoryCustomRepository = bookingHistoryCustomRepository;
        this.accountService = accountService;
    }

    public BookingHistoryResponse searchAndFilterBookingHistory(String userId, String roleName, SearchModel searchModel) {
        List<BookingHistoryModel> bookingHistoryModelList = handleSearchAndFilterBookingHistory(userId, roleName, searchModel);
        if (bookingHistoryModelList == null || bookingHistoryModelList.size() == 0) {
            return BookingHistoryResponse.builder().page(0).maxResult(0).message("There was no items in data").build();
        }

        int maxResult = bookingHistoryModelList.size();
        int pageValue = searchModel.getPage() != null ? searchModel.getPage() : 1;
        int offSetValue = searchModel.getItemsPerPage() != null ? searchModel.getItemsPerPage() : 10;

        if ((pageValue - 1) * offSetValue >= maxResult) {
            pageValue = 1;
        }
        int startIndex = Math.max((pageValue - 1) * offSetValue - 1, 0);
        int endIndex = Math.min((pageValue * offSetValue), maxResult);

        return BookingHistoryResponse.builder()
                .page(pageValue)
                .maxResult(maxResult)
                .data(bookingHistoryModelList.subList(startIndex, endIndex))
                .build();
    }

    public List<BookingHistoryModel> handleSearchAndFilterBookingHistory(String userId, String roleName, SearchModel searchModel) {
        List<BookingHistoryEntity> bookingEntities;
        switch (roleName) {
            case RoleProperties.ROLE_USER:
                bookingEntities = bookingHistoryCustomRepository.getAllBookingHistoryOfUser(userId);
                break;
            case RoleProperties.ROLE_OWNER:
                bookingEntities = bookingHistoryCustomRepository.getAllBookingHistoryOfOwner(userId);
                break;
            default:
                return null;
        }

        List<BookingHistoryModel> bookingHistoryModels = bookingEntities.stream().map(booking -> {
            String createBy = booking.getCreatedBy().equals(userId) ? "You" : accountService.getRoleFromUserId(booking.getCreatedBy());
            return getBookingHistoryModelFromBookingHistoryEntityAndCreatedBy(booking, "You");
        }).collect(Collectors.toList());
        if(searchModel != null){
            bookingHistoryModels = searchBookingHistories(searchModel.getKeyword(), bookingHistoryModels);
            bookingHistoryModels = filterBookingHistories(searchModel.getFilter(), bookingHistoryModels);
            bookingHistoryModels = sortBookingHistories(searchModel.getSort(), bookingHistoryModels);
        }
        return bookingHistoryModels;
    }

    private List<BookingHistoryModel> searchBookingHistories(String keyword, List<BookingHistoryModel> bookingHistoryModels) {
        String keywordValue = keyword != null && keyword.trim().length() > 0 ? keyword.trim().toLowerCase() : null;

        if (keywordValue == null) {
            return bookingHistoryModels;
        }
        return bookingHistoryModels.stream().filter(bookingHistoryModel -> bookingHistoryModel.getBigYardName().toLowerCase().contains(keywordValue)
                || bookingHistoryModel.getAddress().toLowerCase().contains(keywordValue)
        ).collect(Collectors.toList());
    }

    private List<BookingHistoryModel> filterBookingHistories(FilterModel filter, List<BookingHistoryModel> bookingHistoryModels) {
        if (filter == null) {
            return bookingHistoryModels;
        }
        if (filter.getField().equals("status")) {
            return bookingHistoryModels.stream().filter(
                    bookingHistoryModel -> bookingHistoryModel.getBookingStatus().equals(filter.getValue())
            ).collect(Collectors.toList());
        }
        return bookingHistoryModels;
    }

    private List<BookingHistoryModel> sortBookingHistories(String columnSort, List<BookingHistoryModel> bookingHistoryModels) {
        if (columnSort == null || columnSort.trim().length() == 0) {
            return bookingHistoryModels;
        }
        String columnName = columnSort.trim().toLowerCase();
        char sort = columnSort.charAt(0);
        if (sort == '+' || sort == '-') {
            columnName = columnName.substring(1);
        } else {
            sort = '+';
        }

        if (columnName.equals("reference")) {
            if (sort == '+') {
                bookingHistoryModels.sort(Comparator.comparingLong(BookingHistoryModel::getReference));
            } else {
                bookingHistoryModels.sort((first, second) -> Long.compare(second.getReference(), first.getReference()));
            }
        }

        if (columnName.equals("createdAt")) {
            if (sort == '+') {
                bookingHistoryModels.sort(Comparator.comparing(BookingHistoryModel::getCreatedAt));
            } else {
                bookingHistoryModels.sort((first, second) -> second.getCreatedAt().compareTo(first.getCreatedAt()));
            }
        }

        if (columnName.equals("price")) {
            if (sort == '+') {
                bookingHistoryModels.sort(Comparator.comparingInt(BookingHistoryModel::getPrice));
            } else {
                bookingHistoryModels.sort((first, second) -> Integer.compare(second.getPrice(), first.getPrice()));
            }
        }
        return bookingHistoryModels;
    }

    public BookingHistoryModel getBookingHistoryModelFromBookingHistoryEntityAndCreatedBy(BookingHistoryEntity bookingHistoryEntity, String createdBy) {
        BookingEntity booking = bookingRepository.findBookingEntityById(bookingHistoryEntity.getBookingId());
        MatchModel matchModel = matchService.transformMatchModelFromBookingEntity(booking);

        return BookingHistoryModel.builder()
                .bookingId(bookingHistoryEntity.getBookingId())
                .bookingStatus(bookingHistoryEntity.getBookingStatus())
                .createdAt(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(bookingHistoryEntity.getCreatedAt()))
                .createdBy(createdBy)
                .note(bookingHistoryEntity.getNote())
                .reference(bookingHistoryEntity.getReference())
                .time(matchModel.getStartTime() + " - " + matchModel.getEndTime())
                .bigYardName(matchModel.getBigYardName())
                .subYardName(matchModel.getSubYardName())
                .type(matchModel.getType())
                .price(matchModel.getPrice())
                .address(matchModel.getBigYardAddress())
                .build();
    }

    public void saveBookingHistory(BookingEntity bookingEntity, String reason, String createdBy) {
        BookingHistoryEntity bookingHistoryEntity = BookingHistoryEntityBuilder.buildFromBookingEntity(bookingEntity, reason);
        bookingHistoryEntity.setCreatedBy(createdBy);
        bookingHistoryRepository.save(bookingHistoryEntity);
    }
}
