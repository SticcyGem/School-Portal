package net.bscs22.schoolportal.mappers

import net.bscs22.schoolportal.dtos.user.UpdateUserRequest
import net.bscs22.schoolportal.dtos.user.UserResponse
import net.bscs22.schoolportal.entities.accounts.Account
import net.bscs22.schoolportal.entities.accounts.UserProfile
import org.mapstruct.*

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
abstract class UserMapper {

    // VIEW MAPPING
    @Mapping(source = "account.accountId", target = "accountId")
    @Mapping(source = "account.status", target = "status")
    @Mapping(source = "profile.firstName", target = "firstName")
    @Mapping(source = "profile.lastName", target = "lastName")
    @Mapping(target = "roles", expression = "java(mapRoles(account))")
    abstract fun toUserResponse(account: Account, profile: UserProfile): UserResponse

    // HELPER FUNCTIONS
    @Named("mapRoles")
    fun mapRoles(account: Account): List<String> {
        return account.roles.map { it.roleName }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract fun updateAccount(req: UpdateUserRequest, @MappingTarget account: Account)

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract fun updateProfile(req: UpdateUserRequest, @MappingTarget profile: UserProfile)
}