package interview.jpmorgan.restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/*
 Simple reservation system, restaurant can only accommodate 4 reservations per hour. Functions to add, view and cancel reservation 

1) Add reservation:
Input- customer name, date and time of reservation 
output- confirmation of the reservation if slot is available or not 

2) View -
Input - date
output - all reservations for the customer

3) Cancel
Input- same as add
output - confirmation on cancellation 

Constrains- restaurant operating hour - 10:00 am to 10:00 pm

Deliverable - No need of db. Utilize map or file
 */

@RestController("/reservations")
@Slf4j
public class ReservationController {

  @Autowired
  ReservationService service;

  @PostMapping("/add")
  public ResponseEntity<Reservation> addNewReservation(
      @RequestParam(name = "dateOfReservation") @DateTimeFormat(iso = ISO.DATE) LocalDate dateOfReservation,
      @RequestParam Integer hourTime, @RequestParam String name) {

    Reservation r = this.service.addNewReservation(this.normalizeInputDate(dateOfReservation, hourTime), name);
    log.info("New reservation added for " + name + ", for date " + r.getTimeOfReservation());
    return ResponseEntity.ok(r);
  }

  @GetMapping("/view")
  public ResponseEntity<List<Reservation>> viewReservations(
      @RequestParam(name = "dateOfReservation") @DateTimeFormat(iso = ISO.DATE) LocalDate dateOfReservation,
      @RequestParam Integer hourTime, @RequestParam(required = false) String name) {
    List<Reservation> rList = this.service.viewReservations(dateOfReservation, name);

    return ResponseEntity.ok(rList);
  }

  @DeleteMapping("/cancel")
  public ResponseEntity<String> cancelReservation(
      @RequestParam(name = "dateOfReservation") @DateTimeFormat(iso = ISO.DATE) LocalDate dateOfReservation,
      @RequestParam Integer hourTime, @RequestParam String name) {
    String resp = this.service.cancelReservation(this.normalizeInputDate(dateOfReservation, hourTime), name);
    return ResponseEntity.ok(resp);
  }

  private LocalDateTime normalizeInputDate(LocalDate input, Integer hour) {
    LocalDateTime output = LocalDateTime.of(input.getYear(), input.getMonth(), input.getDayOfMonth(), hour, 0);
    return output;
  }

}
