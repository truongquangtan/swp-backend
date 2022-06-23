package com.swp.backend.service;

import com.swp.backend.api.v1.book.cancel_booking.CancelBookingRequest;
import com.swp.backend.exception.InactivateProcessException;
import com.swp.backend.myrepository.SlotCustomRepository;
import com.swp.backend.repository.SlotRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InactivationService {

    private SlotCustomRepository slotCustomRepository;
    private SlotRepository slotRepository;
    private BookingService bookingService;
    private CancelBookingService cancelBookingService;

    public void inactivateSlot(String ownerId, int slotId)
    {
        if(!slotCustomRepository.findOwnerIdFromSlotId(slotId).equals(ownerId))
        {
            throw new InactivateProcessException("The owner is not author of this slot.");
        }

        if(slotRepository.findSlotEntityByIdAndActive(slotId, false) != null)
        {
            throw new InactivateProcessException("The slot is already inactive.");
        }

        CancelBookingRequest

        cancelBookingService.cancelBooking();

    }
}
