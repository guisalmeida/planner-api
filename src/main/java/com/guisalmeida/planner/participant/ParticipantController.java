package com.guisalmeida.planner.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {
    @Autowired
    private ParticipantRepository participantRepository;

    @PostMapping("/{participantId}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID participantId, @RequestBody ParticipantRequestPayload payload) {
        Optional<Participant> participant = this.participantRepository.findById(participantId);

        if (participant.isPresent()) {
            Participant rawParticipant = participant.get();
            rawParticipant.setConfirmed(true);
            rawParticipant.setName(payload.name());

            this.participantRepository.save(rawParticipant);
            return ResponseEntity.ok(rawParticipant);
        }

        return ResponseEntity.notFound().build();
    }
}
