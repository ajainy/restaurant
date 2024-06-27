package interview.jpmorgan.restaurant;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class Reservation {

  public Reservation() {
    reservationId = UUID.randomUUID().toString();
  }

  private String reservationId;
  private String name;
  private LocalDateTime timeOfReservation;

}
