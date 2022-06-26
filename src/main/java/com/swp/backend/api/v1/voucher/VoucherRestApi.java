package com.swp.backend.api.v1.voucher;

import com.google.gson.Gson;
import com.swp.backend.api.v1.owner.voucher.VoucherResponse;
import com.swp.backend.model.RequestPageModel;
import com.swp.backend.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class VoucherRestApi {
    private VoucherService voucherService;
    private Gson gson;

    @PostMapping("owners/{ownerId}/vouchers")
    public ResponseEntity<String> getAllVoucherAvailable(@RequestBody(required = false) RequestPageModel requestPageModel, @PathVariable String ownerId) {
        try {
            VoucherResponse response;
            if (requestPageModel == null) {
                response = voucherService.getAllVoucherByOwnerId(ownerId, null, null);
            } else {
                response = voucherService.getAllVoucherByOwnerId(ownerId, requestPageModel.getItemsPerPage(), requestPageModel.getPage());
            }
            return ResponseEntity.ok().body(gson.toJson(response));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
