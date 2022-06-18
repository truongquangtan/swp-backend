package com.swp.backend.service;

import com.swp.backend.entity.BookingEntity;
import com.swp.backend.entity.SlotEntity;
import com.swp.backend.entity.SubYardEntity;
import com.swp.backend.model.MatchModel;
import com.swp.backend.model.YardModel;
import com.swp.backend.myrepository.SlotCustomRepository;
import com.swp.backend.myrepository.SubYardCustomRepository;
import com.swp.backend.repository.SlotRepository;
import com.swp.backend.repository.SubYardRepository;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MatchService {
    private SlotCustomRepository slotCustomRepository;
    private SlotRepository slotRepository;
    private YardService yardService;
    private SubYardCustomRepository subYardCustomRepository;
    private SubYardRepository subYardRepository;

    public MatchModel getMatchModelFromBookingEntity(BookingEntity bookingEntity) {
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Timestamp bookingDate = bookingEntity.getDate();
        LocalDate date = LocalDate.ofInstant(bookingDate.toInstant(), ZoneId.of(DateHelper.VIETNAM_ZONE));

        int slotId = bookingEntity.getSlotId();
        String subYardId = slotCustomRepository.findSubYardIdFromSlotId(slotId);
        String typeYard = subYardCustomRepository.findTypeYardFromSubYardId(subYardId);
        SubYardEntity subYardEntity = subYardRepository.getSubYardEntityByIdAndActive(subYardId, true);
        String yardId = slotCustomRepository.findYardIdFromSlotId(slotId);
        SlotEntity slotEntity = slotRepository.findSlotEntityByIdAndActive(slotId, true);
        YardModel yardModel = yardService.getYardModelFromYardId(yardId);
        return MatchModel.builder().bigYardAddress(yardModel.getAddress())
                .bigYardName(yardModel.getName())
                .date(date.format(dateFormatter))
                .district(yardModel.getDistrictName())
                .startTime(slotEntity.getStartTime().format(hourFormatter))
                .endTime(slotEntity.getEndTime().format(hourFormatter))
                .type(typeYard)
                .subYardName(subYardEntity.getName())
                .province(yardModel.getProvince())
                .bookAt((new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(bookingEntity.getBookAt()))
                .bookingReference(bookingEntity.getReference())
                .bigYardId(yardId)
                .subYardId(subYardId)
                .slotId(slotId)
                .bookingStatus(bookingEntity.getStatus())
                .build();
    }

    public List<MatchModel> getListMatchModelFromListBookingEntity(List<BookingEntity> bookingEntities) {
        List<MatchModel> result = new ArrayList<>();

        for (BookingEntity bookingEntity : bookingEntities) {
            MatchModel model = getMatchModelFromBookingEntity(bookingEntity);
            result.add(model);
        }

        return result;
    }
}
