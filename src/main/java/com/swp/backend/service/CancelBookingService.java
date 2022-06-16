package com.swp.backend.service;

import com.swp.backend.api.v1.book.cancel_booking.CancelBookingRequest;
import com.swp.backend.constance.BookingStatus;
import com.swp.backend.entity.*;
import com.swp.backend.exception.CancelBookingProcessException;
import com.swp.backend.myrepository.BookingCustomRepository;
import com.swp.backend.repository.*;
import com.swp.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class CancelBookingService {
    private YardService yardService;
    private SubYardRepository subYardRepository;
    private SlotRepository slotRepository;
    private BookingRepository bookingRepository;
    private YardRepository yardRepository;
    private EmailService emailService;
    private AccountRepository accountRepository;
    public static final int PREVENT_CANCEL_BOOKING_IN_MINUTE = 30;

    public void cancelBooking(String userId, int bookingId, CancelBookingRequest request)
    {
        BookingEntity booking = getBookingEntity(bookingId);
        bookingIsOfUserFilter(booking, userId);
        yardIsActiveAndNotDeleted(request.getYardId());
        SubYardEntity subYard = subYardIsActiveFilter(request.getSubYardId());
        SlotEntity slot = slotIdIsActiveFilter(request.getSlotId());
        bookingStatusIsSuccessFilter(booking);
        slotTimeStartIsNotOverPreventTimeForCancel(booking, slot);

        cancelBookingProcess(userId, slot, subYard, booking, request);
    }

    private void slotTimeStartIsNotOverPreventTimeForCancel(BookingEntity booking, SlotEntity slot) {
        if(DateHelper.isToday(booking.getDate()))
        {
            LocalTime slotStartTime = slot.getStartTime();
            LocalTime now = LocalTime.now(ZoneId.of(DateHelper.VIETNAM_ZONE));
            now = now.plusMinutes(PREVENT_CANCEL_BOOKING_IN_MINUTE);
            if(now.isAfter(slotStartTime))
            {
                throw new CancelBookingProcessException("The match will be start after " + PREVENT_CANCEL_BOOKING_IN_MINUTE + "minute. You cannot cancel that.");
            }
        }
    }

    private BookingEntity getBookingEntity(int bookingId)
    {
        BookingEntity booking = bookingRepository.getBookingEntityById(bookingId);
        if(booking == null)
        {
            throw new CancelBookingProcessException("Can not get booking entity from bookingId");
        }
        return booking;
    }
    private void bookingIsOfUserFilter(BookingEntity booking, String userId)
    {
        if(!booking.getAccountId().equals(userId))
        {
            throw new CancelBookingProcessException("The user is not the author this booking entity.");
        }
        return;
    }
    private void bookingStatusIsSuccessFilter(BookingEntity booking)
    {
        if(!booking.getStatus().equals(BookingStatus.SUCCESS))
        {
            throw new CancelBookingProcessException("The booking entity of the request is not a success booking.");
        }
        return;
    }
    private void yardIsActiveAndNotDeleted(String yardId)
    {
        if(!yardService.isAvailableYard(yardId))
        {
            throw new CancelBookingProcessException("Your booking is canceled before, the yard is inactivated or deleted. Or yard is not found.");
        }
        return;
    }
    private SubYardEntity subYardIsActiveFilter(String subYardId)
    {
        SubYardEntity subYard = subYardRepository.getSubYardEntityByIdAndActive(subYardId, true);
        if(subYard == null)
        {
            throw new CancelBookingProcessException("Your booking is canceled before, the sub-yard is inactivated by the owner. Or subYard not found.");
        }
        return subYard;
    }
    private SlotEntity slotIdIsActiveFilter(int slotId)
    {
        SlotEntity slot = slotRepository.findSlotEntityByIdAndActive(slotId, true);
        if(slot == null)
        {
            throw new CancelBookingProcessException("Your booking is canceled before because the slot is inactivated by owner. Or slot not found.");
        }
        return slot;
    }

    private void cancelBookingProcess(String userId, SlotEntity slot, SubYardEntity subYard, BookingEntity booking, CancelBookingRequest request)
    {
        YardEntity yard = yardRepository.findYardEntitiesById(request.getYardId());
        String ownerId = yard.getOwnerId();

        BookingEntity modifiedBooking = saveBookingCanceledInformation(booking, request.getReason());
        sendMailToOwner(userId, ownerId, request.getYardId(), subYard, modifiedBooking, slot, request.getReason());
    }

    private BookingEntity saveBookingCanceledInformation(BookingEntity booking, String reason)
    {
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        booking.setStatus(BookingStatus.CANCELED);
        booking.setBookAt(now);
        booking.setNote(reason);
        bookingRepository.save(booking);
        return booking;
    }
    private void sendMailToOwner(String userId,
                                 String ownerId,
                                 String yardId,
                                 SubYardEntity subyard,
                                 BookingEntity booking,
                                 SlotEntity slot,
                                 String reason)
    {
        AccountEntity user = accountRepository.findUserEntityByUserId(userId);
        String destination = accountRepository.findUserEntityByUserId(ownerId).getEmail();
        String htmlTemplate = getHtmlTemplate(yardService.getYardFullAddress(yardId),
                                            user.getFullName(),
                                 subyard.getName() + " - " + subyard.getTypeYard(),
                                            reason,
                                            booking,
                                            slot);
        emailService.sendHtmlTemplateMessage(destination, "Cancel booking from " + user.getFullName(), htmlTemplate);
    }
    private static String getHtmlTemplate(String yardAddress,
                                          String userName,
                                          String subYardName,
                                          String reason,
                                          BookingEntity booking,
                                          SlotEntity slot)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
        String result = "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
    "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:18px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
    "<hr>" +
    "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:16px; margin: 0; padding: 2px; text-align: center;\">There was a booking canceled by user <span style=\"color: green ; font-style: italic;\">"+userName+"</span> </p>" +
    "<table border=\"1\" style=\"margin: 0 auto;\">" +
        "<tr>" +
            "<td>Yard Address</td>" +
            "<td>"+yardAddress+"</td>" +
        "</tr>" +
        "<tr>" +
            "<td>SubYard</td>" +
            "<td>"+ subYardName +"</td>" +
        "</tr>" +
        "<tr>" +
            "<td>Start Time</td>" +
            "<td>"+ formatter.format(slot.getStartTime()) +"</td>" +
        "</tr>" +
        "<tr>" +
            "<td>End Time</td>" +
            "<td>"+ formatter.format(slot.getEndTime()) +"</td>" +
        "</tr>" +
        "<tr>" +
            "<td>Date</td>" +
            "<td>"+ new SimpleDateFormat("dd/MM/yyyy").format(booking.getDate()) +"</td>" +
        "</tr>" +
        "<tr>" +
            "<td>Reason</td>" +
            "<td>"+ reason +"</td>" +
        "</tr>" +
        "<tr>" +
            "<td>Booking Note</td>" +
            "<td>"+ booking.getNote() +"</td>" +
        "</tr>" +
        "<tr>" +
            "<td>Canceled At</td>" +
            "<td>"+ new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(booking.getBookAt()) +"</td>" +
        "</tr>" +
    "</table>" +
    "<p style=\"text-align: center;\">--------------</p>";
        return result;
    }
}
