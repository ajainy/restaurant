package interview.jpmorgan.restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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

@Service
@Slf4j
public class ReservationService {

  private static int NUM_OF_TABLES = 4;
  private static int FIRST_RESERVATION_TIME = 10; // 10am
  private static int LAST_RESERVATION_TIME = 21; // 9pm

  private Map<LocalDateTime, List<Reservation>> reservations = new HashMap<>();

  public Reservation addNewReservation(LocalDateTime dateOfReservation, String name) {

    if (!this.isValidDateTime(dateOfReservation)) {
      throw new ValidationException("Requested reservation date/time is not valid : " + dateOfReservation);
    }

    this.checkReservationAvailableForCustomer(dateOfReservation, name);

    return this.addReservation(dateOfReservation, name);
  }

  public List<Reservation> viewReservations(LocalDate dateOfReservation, String name) {

    final List<Reservation> resp = new ArrayList<>();

    // Make list of that whole day reservations
    reservations.forEach((currDate, reservs) -> {
      if (dateOfReservation.getDayOfYear() == currDate.getDayOfYear()
          && dateOfReservation.getYear() == currDate.getYear()) {
        resp.addAll(reservs);
      }
    });

    if (!ObjectUtils.isEmpty(name)) {
      resp.removeIf(reserv -> !reserv.getName().equalsIgnoreCase(name));
    }

    return resp;
  }

  public String cancelReservation(LocalDateTime dateOfReservation, String name) {
    List<Reservation> reservationsForHour = this.reservations.get(dateOfReservation);

    boolean result = false;
    if (null != reservationsForHour && !reservationsForHour.isEmpty()) {
      result = reservationsForHour.removeIf(reserv -> reserv.getName().equalsIgnoreCase(name));
    }

    if (result) {
      return "Reservation is canceled successfully for date " + dateOfReservation + " for customer " + name;
    }

    throw new NotFoundException("No Reservation found for date " + dateOfReservation + " for customer " + name);

  }

  private boolean isValidDateTime(LocalDateTime dateOfReservation) {
    LocalDateTime currentDateTime = LocalDateTime.now();

    boolean isFutureDateTime = dateOfReservation.isAfter(currentDateTime);

    if (isFutureDateTime) {
      // We allow 10am and last reservation till 9pm.
      // at 10pm, restaurant closes.
      return dateOfReservation.getHour() >= FIRST_RESERVATION_TIME
          && dateOfReservation.getHour() <= LAST_RESERVATION_TIME;
    }
    return false;

  }

  private void checkReservationAvailableForCustomer(LocalDateTime dateOfReservation, String name) {

    List<Reservation> existingReservations = this.reservations.get(dateOfReservation);

    if (null != existingReservations) {
      if (existingReservations.size() >= NUM_OF_TABLES) {
        throw new ValidationException("No more reservation is available at same time of " + dateOfReservation);
      }

      for (Reservation reservation : existingReservations) {
        if (reservation.getName().equalsIgnoreCase(name)) {
          throw new ValidationException(
              "Customer with name " + name + " already has reservation at same time of " + dateOfReservation);
        }
      }
    }
  }

  private Reservation addReservation(LocalDateTime dateOfReservation, String name) {

    Reservation newReservation = new Reservation();
    // newReservation.reservationId = UUID.randomUUID().toString();
    newReservation.setName(name);
    newReservation.setTimeOfReservation(dateOfReservation);

    // this.reservations.put(dateOfReservation, null)

    List<Reservation> existingReservations = this.reservations.get(dateOfReservation);

    if (null == existingReservations) {
      existingReservations = new ArrayList<>();
    }
    existingReservations.add(newReservation);

    this.reservations.put(dateOfReservation, existingReservations);

    return newReservation;
  }

}
