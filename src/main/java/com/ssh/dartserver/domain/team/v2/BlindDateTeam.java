package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.team.domain.vo.Name;
import com.ssh.dartserver.domain.team.domain.vo.TeamDescription;
import com.ssh.dartserver.domain.user.domain.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BlindDateTeam {
    private Long id;
    private final User user;  // not null TODO
    private final Name name;  // not empty
    private final TeamDescription description;
    private final boolean isVisibleToSameUniversity;
    private final List<Long> regionIds;  // not empty
    private final List<String> imageUrls;  // not empty

    @Builder
    public BlindDateTeam(Name name,
                         User user,
                         TeamDescription description,
                         boolean isVisibleToSameUniversity,
                         List<Long> regionIds,
                         List<String> imageUrls) {
        this.name = name;
        this.user = user;
        this.description = description;
        this.isVisibleToSameUniversity = isVisibleToSameUniversity;
        this.regionIds = regionIds;
        this.imageUrls = imageUrls;
    }
}
