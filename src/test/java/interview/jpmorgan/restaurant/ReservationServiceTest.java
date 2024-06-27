package interview.jpmorgan.restaurant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ReservationServiceTest {

  @Test
  public void testAddReservation() {
    ReservationService service = new ReservationService();
    LocalDateTime dateOfReservation = LocalDateTime.now();
    String name = "ashish";

    List<Reservation> reservs = service.viewReservations(dateOfReservation.toLocalDate(), name);
    assertTrue(reservs.isEmpty());

    dateOfReservation = LocalDateTime.of(2024, 6, 27, 11, 0); // 2024-6-27, 11am
    Reservation newReserv = service.addNewReservation(dateOfReservation, name);
    assertNotNull(newReserv);

    reservs = service.viewReservations(dateOfReservation.toLocalDate(), name);
    assertTrue(!reservs.isEmpty());
    assertTrue(reservs.size() == 1);

    // invalid time range
    try {
      dateOfReservation = LocalDateTime.of(2024, 6, 27, 22, 0); // 2024-6-27, 10 PM
      service.addNewReservation(dateOfReservation, name);
      fail("Should have thrown exception");

    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }

    // at same time, same customer can't have two reservations.
    try {
      dateOfReservation = LocalDateTime.of(2024, 6, 27, 11, 0); // 2024-6-27, 10 PM
      service.addNewReservation(dateOfReservation, name);
      fail("Should have thrown exception");

    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }

  }

  private void fillUpReservations(ReservationService service) {
    String name = "ashish";
    for (int i = 1; i < 31; i++) {
      for (int j = 10; j < 22; j++) {
        LocalDateTime dateOfReservation = LocalDateTime.of(2024, 7, i, j, 0); // 2024-7-[1-30]-[9-21]-0
        for (int table = 1; table < 5; table++) {
          Reservation newReserv = service.addNewReservation(dateOfReservation, name + table);
          assertNotNull(newReserv);
        }
      }
    }

    LocalDate dateOfReservation = LocalDate.of(2024, 7, 1);
    List<Reservation> reservs = service.viewReservations(dateOfReservation, "ashish1");
    assertTrue(!reservs.isEmpty());
    assertTrue(reservs.size() == 12);

    reservs = service.viewReservations(dateOfReservation, null);
    assertTrue(!reservs.isEmpty());
    assertTrue(reservs.size() == 48);
  }

  @Test
  public void testCancelReservation() {
    try {
      ReservationService service = new ReservationService();
      LocalDate dateOfReservation = LocalDate.now();
      String name = "ashish";

      List<Reservation> reservs = service.viewReservations(dateOfReservation, name);
      assertTrue(reservs.isEmpty());

      this.fillUpReservations(service);

      LocalDateTime dateTimeOfReservation = LocalDateTime.of(2024, 7, 1, 10, 0); // 2024-6-27, 10 PM
      String resp = service.cancelReservation(dateTimeOfReservation, "ashish1");
      System.out.println(resp);

      reservs = service.viewReservations(dateTimeOfReservation.toLocalDate(), "ashish1");
      assertTrue(!reservs.isEmpty());
      assertTrue(reservs.size() == 11);

      // Try to cancel again.. same reservation.
      resp = service.cancelReservation(dateTimeOfReservation, "ashish1");
      System.out.println(resp);
      assertTrue(resp.startsWith("No Reservation found"));

    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      fail(ex.getMessage());
    }
  }

}
