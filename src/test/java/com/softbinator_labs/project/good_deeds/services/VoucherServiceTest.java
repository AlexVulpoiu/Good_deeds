package com.softbinator_labs.project.good_deeds.services;

import com.softbinator_labs.project.good_deeds.dtos.VoucherDto;
import com.softbinator_labs.project.good_deeds.models.Voucher;
import com.softbinator_labs.project.good_deeds.repositories.VoucherRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@DataJpaTest
public class VoucherServiceTest {

    @InjectMocks
    private VoucherService voucherService;

    @Mock
    private VoucherRepository voucherRepository;

    @Test
    public void getAllVouchersTest() {

        Voucher firstVoucher = Voucher.builder()
                .discount(20)
                .company("Emag")
                .price(60)
                .build();
        Voucher secondVoucher = Voucher.builder()
                .discount(10)
                .company("Nike")
                .price(40)
                .build();
        Voucher thirdVoucher = Voucher.builder()
                .discount(30)
                .company("Lidl")
                .price(100)
                .build();
        List<Voucher> voucherList = List.of(firstVoucher, secondVoucher, thirdVoucher);

        VoucherDto firstVoucherDto = VoucherDto.builder()
                .discount(20)
                .company("Emag")
                .price(60)
                .build();
        VoucherDto secondVoucherDto = VoucherDto.builder()
                .discount(10)
                .company("Nike")
                .price(40)
                .build();
        VoucherDto thirdVoucherDto = VoucherDto.builder()
                .discount(30)
                .company("Lidl")
                .price(100)
                .build();

        List<VoucherDto> voucherDtoList = List.of(firstVoucherDto, secondVoucherDto, thirdVoucherDto);
        when(voucherRepository.findAll()).thenReturn(voucherList);

        ResponseEntity<?> responseEntity = voucherService.getAllVouchers();
        Object responseEntityBody = responseEntity.getBody();

        if(responseEntityBody instanceof List) {
            Assertions.assertEquals(3, ((List<?>) responseEntityBody).size());
            Assertions.assertEquals(voucherDtoList, responseEntityBody);
        }
    }

    @Test
    public void addVoucherShouldWorkTest() {

        Voucher voucher = Voucher.builder()
                .discount(20)
                .company("Emag")
                .price(60)
                .build();
        List<Voucher> vouchers = new ArrayList<>();
        when(voucherRepository.findAll()).thenReturn(vouchers);
        when(voucherRepository.save(voucher)).thenReturn(voucher);
        ResponseEntity<?> responseEntity = new ResponseEntity<>("Voucher successfully added!", HttpStatus.OK);

        Assertions.assertEquals(responseEntity, voucherService.addVoucher(voucher));
    }

    @Test
    public void addVoucherShouldNotWorkTest() {

        Voucher voucher = Voucher.builder()
                .discount(20)
                .company("Emag")
                .price(60)
                .build();
        List<Voucher> vouchers = List.of(voucher);
        when(voucherRepository.findAll()).thenReturn(vouchers);
        when(voucherRepository.save(voucher)).thenReturn(voucher);
        ResponseEntity<?> responseEntity =
                new ResponseEntity<>("There is already a voucher with the same content!", HttpStatus.BAD_REQUEST);

        Assertions.assertEquals(responseEntity, voucherService.addVoucher(voucher));
    }
}
