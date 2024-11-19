package ua.edu.ukma.cleaning.commercial.proposal;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommercialProposalMapper {
    CommercialProposalDto toDto(CommercialProposalEntity proposal);

    CommercialProposalEntity toEntity(CommercialProposalDto proposal);

    List<CommercialProposalDto> toDtoList(List<CommercialProposalEntity> entities);
}
