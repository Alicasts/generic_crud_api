package com.alicasts.generic_crud.api.mapper;

import com.alicasts.generic_crud.api.dto.UserUpdateRequest;
import com.alicasts.generic_crud.model.Sex;
import com.alicasts.generic_crud.model.UserPatch;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserPatchMapper {

    UserPatch toPatch(UserUpdateRequest req);

    default Sex mapGender(String gender) {
        return gender == null ? null : Sex.valueOf(gender);
    }
}
