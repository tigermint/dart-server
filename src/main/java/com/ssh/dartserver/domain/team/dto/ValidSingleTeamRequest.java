package com.ssh.dartserver.domain.team.dto;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TeamRequestValidator.class)
public @interface ValidSingleTeamRequest {
    String message() default "팀원이 없을 때는 함께 참여할 친구 정보가 있어야 합니다.";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

}
