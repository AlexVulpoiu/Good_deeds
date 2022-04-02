package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.dtos.VoucherDto;
import com.softbinator_labs.project.good_deeds.models.Voucher;
import com.softbinator_labs.project.good_deeds.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {

    private final VoucherRepository voucherRepository;

    @Autowired
    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public ResponseEntity<?> getAllVouchers() {

        List<Voucher> vouchers = voucherRepository.findAll();
        if(vouchers.isEmpty()) {
            return new ResponseEntity<>("There are no vouchers available yet!", HttpStatus.OK);
        }

        List<VoucherDto> vouchersDto = new ArrayList<>();
        for(Voucher voucher: vouchers) {
            VoucherDto voucherDto = VoucherDto.builder()
                    .id(voucher.getId())
                    .discount(voucher.getDiscount())
                    .company(voucher.getCompany())
                    .price(voucher.getPrice())
                    .build();
            vouchersDto.add(voucherDto);
        }

        return new ResponseEntity<>(vouchersDto, HttpStatus.OK);
    }

    public ResponseEntity<?> getVoucher(Long id) {

        Optional<Voucher> voucher = voucherRepository.findById(id);
        if(voucher.isEmpty()) {
            return new ResponseEntity<>("Voucher with id = " + id + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        VoucherDto voucherDto = VoucherDto.builder()
                .id(voucher.get().getId())
                .discount(voucher.get().getDiscount())
                .company(voucher.get().getCompany())
                .price(voucher.get().getPrice())
                .build();

        return new ResponseEntity<>(voucherDto, HttpStatus.OK);
    }

    public ResponseEntity<?> addVoucher(Voucher newVoucher) {

        List<Voucher> vouchers = voucherRepository.findAll();
        for(Voucher voucher: vouchers) {
            if(voucher.equals(newVoucher)) {
                return new ResponseEntity<>("There is already a voucher with the same content!", HttpStatus.BAD_REQUEST);
            }
        }

        Voucher voucher = Voucher.builder()
                .discount(newVoucher.getDiscount())
                .company(newVoucher.getCompany())
                .price(newVoucher.getPrice())
                .build();
        voucherRepository.save(voucher);

        return new ResponseEntity<>("Voucher successfully added!", HttpStatus.OK);
    }

    public ResponseEntity<?> editVoucher(Long id, Voucher editVoucher) {

        Optional<Voucher> voucher = voucherRepository.findById(id);
        if(voucher.isEmpty()) {
            return new ResponseEntity<>("Voucher with id " + id + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        Voucher currentVoucher = voucher.get();
        currentVoucher.setDiscount(editVoucher.getDiscount());
        currentVoucher.setCompany(editVoucher.getCompany());
        currentVoucher.setPrice(editVoucher.getPrice());
        voucherRepository.save(currentVoucher);

        VoucherDto voucherDto = VoucherDto.builder()
                .id(currentVoucher.getId())
                .discount(currentVoucher.getDiscount())
                .company(currentVoucher.getCompany())
                .price(currentVoucher.getPrice())
                .build();

        return new ResponseEntity<>(voucherDto, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteVoucher(Long id) {

        Optional<Voucher> voucher = voucherRepository.findById(id);
        if(voucher.isEmpty()) {
            return new ResponseEntity<>("Voucher with id " + id + " doesn't exist!", HttpStatus.NOT_FOUND);
        }

        Voucher currentVoucher = voucher.get();
        voucherRepository.delete(currentVoucher);

        return new ResponseEntity<>("Voucher successfully deleted!", HttpStatus.OK);
    }
}
