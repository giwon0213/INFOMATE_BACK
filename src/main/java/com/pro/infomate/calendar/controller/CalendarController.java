package com.pro.infomate.calendar.controller;


import com.pro.infomate.calendar.common.ResponseDTO;
import com.pro.infomate.calendar.dto.CalendarDTO;
import com.pro.infomate.calendar.dto.CalendarSummaryDTO;
import com.pro.infomate.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
@Slf4j
public class CalendarController {

    private final CalendarService calendarService;

    // test success 팀, 회사전체 캘린더 추가 예정
    @GetMapping("/list/{memberId}")
    public ResponseEntity<ResponseDTO> findAll(@PathVariable int memberId){
        log.info("[CalendarController](findAll) memberId : {}",memberId);

        return ResponseEntity.ok()
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(calendarService.findAll(memberId))
                        .build());
    }

    // test success
    @GetMapping("/mylist/{memberCode}")
    public ResponseEntity<ResponseDTO> myCalendarList(@PathVariable int memberCode){
        log.info("[CalendarController](myCalendarList) memberCode : {}",memberCode);

        return ResponseEntity.ok()
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(calendarService.myCalendarList(memberCode))
                        .build());
    }


    // test success
    @GetMapping("/{calendarId}")
    public ResponseEntity<ResponseDTO> findById(@PathVariable Integer calendarId){
        log.info("[CalendarController](findById) calendarId : {}",calendarId);

        return ResponseEntity.ok()
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(calendarService.findById(calendarId))
                        .build());
    }

    // test success
    @PostMapping("/regist")
    public ResponseEntity<ResponseDTO> saveByCalendar(@RequestBody CalendarDTO calendar){
        log.info("[CalendarController](saveByCalendar) calendar : {}",calendar);

        calendar.setCreateDate(LocalDateTime.now());

        log.info("[CalendarController](saveByCalendar) calendar : {}",calendar);

        calendarService.saveByCalendar(calendar);

        return ResponseEntity.ok()
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .build());
    }

    // test success
    @PatchMapping("/update")
    public ResponseEntity<ResponseDTO> updateByCalendar(@RequestBody CalendarDTO calendar){

        log.info("[CalendarController](updateByCalendar) calendar : {}",calendar);

        calendarService.updateById(calendar);

        return ResponseEntity.ok()
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .build());
    }

    // test success
    @PatchMapping("/updateDafault")
    public ResponseEntity<ResponseDTO> updateDefaultByCalendar(@RequestBody CalendarDTO calendarDTO){
        log.info("[CalendarController](updateDefaultByCalendar) calendarDTO: {} ",calendarDTO);
        calendarService.updateDefaultCalender(calendarDTO); // userId 수정 요망
        return ResponseEntity.ok()
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .build());
    }

    // test success
    @DeleteMapping("/delete/{calendarId}")
    public ResponseEntity<ResponseDTO> deleteByCalendar(@PathVariable Integer calendarId){
        log.info("[CalendarController](findSummaryCalendar) calendarId : ", calendarId);

        calendarService.deleteById(calendarId);
        return ResponseEntity.ok()
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .build());
    }


    // test success
    @GetMapping("/openCalendarList")
    public ResponseEntity<ResponseDTO> findOpenCalendarList(){
        return ResponseEntity.ok()
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(calendarService.openCalendarList())
                        .build());
    }

    // test success
    @GetMapping("/summary/{memberCode}")
    public ResponseEntity<ResponseDTO> findSummaryCalendar(@PathVariable int memberCode){

        log.info("[CalendarController](findSummaryCalendar) memberCode : ", memberCode);

        List<CalendarSummaryDTO> calendarSummaryDTOList =
                calendarService.findSummaryCalendar(memberCode);

        log.info("[CalendarController](findSummaryCalendar) calendarSummaryDTOList : ", calendarSummaryDTOList);

        return ResponseEntity.ok()
                .body(ResponseDTO.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .data(calendarSummaryDTOList)
                        .build());
    }

}
