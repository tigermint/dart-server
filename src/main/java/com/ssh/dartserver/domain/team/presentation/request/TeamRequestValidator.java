package com.ssh.dartserver.domain.team.presentation.request;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TeamRequestValidator implements ConstraintValidator<ValidSingleTeamRequest, TeamRequest> {
    @Override
    public boolean isValid(TeamRequest teamRequest, ConstraintValidatorContext context) {
        if (teamRequest.getUserIds().isEmpty()) {
            return teamRequest.getSingleTeamFriends() != null && !teamRequest.getSingleTeamFriends().isEmpty();
        }
        return true;
    }
}
