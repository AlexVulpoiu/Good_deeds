package com.softbinator_labs.project.good_deeds.controllers;

import com.softbinator_labs.project.good_deeds.models.Voucher;
import com.softbinator_labs.project.good_deeds.services.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    @Autowired
    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllVouchers() {
        return voucherService.getAllVouchers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucher(@PathVariable Long id) {
        return voucherService.getVoucher(id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addVoucher(@Valid @RequestBody Voucher newVoucher) {
        return voucherService.addVoucher(newVoucher);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editVoucher(@PathVariable Long id, @Valid @RequestBody Voucher editVoucher) {
        return voucherService.editVoucher(id, editVoucher);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteVoucher(@PathVariable Long id) {
        return voucherService.deleteVoucher(id);
    }
}
