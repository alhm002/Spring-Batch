package org.match.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
public class EntrySpectateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matchId;

    private LocalDateTime entryTime;

    private String gate;

    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    @Embedded
    private SeatLocation seatLocation;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "spectator_id")
    private Spectateur spectateur;
}
