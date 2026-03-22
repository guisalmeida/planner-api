package com.guisalmeida.planner.trip;

import com.guisalmeida.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private TripRepository tripRepository;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        Trip newTrip = new Trip(payload);

        this.tripRepository.save(newTrip);
        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID tripId) {
        Optional<Trip> trip = this.tripRepository.findById(tripId);
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<Trip> updateTripDetails(@PathVariable UUID tripId, @RequestBody TripRequestPayload payload) {
        Optional<Trip> trip = this.tripRepository.findById(tripId);

        if (trip.isPresent()) {
            Trip updatedTrip = trip.get();

            updatedTrip.setDestination(payload.destination());
            updatedTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            updatedTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));

            this.tripRepository.save(updatedTrip);
            this.participantService.triggerConfirmationEmailToParticipants(tripId);

            return ResponseEntity.ok(updatedTrip);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{tripId}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID tripId) {
        Optional<Trip> trip = this.tripRepository.findById(tripId);

        if (trip.isPresent()) {
            Trip updatedTrip = trip.get();

            updatedTrip.setConfirmed(true);

            this.tripRepository.save(updatedTrip);
            return ResponseEntity.ok(updatedTrip);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{tripId}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID tripId, @RequestBody ParticipantRequestPayload payload) {
        Optional<Trip> trip = this.tripRepository.findById(tripId);

        if (trip.isPresent()) {
            Trip updatedTrip = trip.get();

            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToEvent(payload.email(), updatedTrip);

            if (updatedTrip.isConfirmed()) {
                this.participantService.triggerConfirmationEmailToParticipant(payload.email());
            }

            return ResponseEntity.ok(participantResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{tripId}/participants")
    public ResponseEntity<List<ParticipantData>> getParticipants(@PathVariable UUID tripId) {
        List<ParticipantData> participantList = this.participantService.getParticipantsFromTrip(tripId);
        return ResponseEntity.ok(participantList);
    }
}
