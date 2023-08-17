package com.ssh.dartserver.domain.team.dto.mapper;

import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.dto.RegionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegionMapper {
    @Mapping(target = "id", source = "region.id")
    @Mapping(target = "name", source = "region.name")
    RegionResponse toRegionResponse(Region region);

}
