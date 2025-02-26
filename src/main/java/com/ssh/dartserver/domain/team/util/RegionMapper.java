package com.ssh.dartserver.domain.team.util;

import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.presentation.response.RegionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegionMapper {
    @Mapping(target = "id", source = "region.id")
    @Mapping(target = "name", source = "region.name")
    RegionResponse toRegionResponse(Region region);

}
