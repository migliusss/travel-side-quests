package com.example.travelsidequests.service

import com.example.travelsidequests.data.Category
import com.example.travelsidequests.data.Country
import com.example.travelsidequests.data.Destination
import com.example.travelsidequests.data.SideQuest
import com.example.travelsidequests.data.SideQuestSource
import com.example.travelsidequests.dto.PageResponse
import com.example.travelsidequests.dto.SideQuestFilterRequest
import com.example.travelsidequests.dto.SideQuestResponse
import com.example.travelsidequests.mapper.toPageResponse
import com.example.travelsidequests.mapper.toResponse
import com.example.travelsidequests.repository.SideQuestRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class GetSideQuestsByFilterService(
    private val sideQuestRepository: SideQuestRepository
) {
    fun execute(
        filter: SideQuestFilterRequest,
        pageable: Pageable,
    ): PageResponse<SideQuestResponse> {

        var spec: Specification<SideQuest> = Specification { _, _, cb -> cb.conjunction() }

        filter.countryId?.let { countryId ->
            spec = spec.and { root, _, cb ->
                cb.equal(
                    root.get<Destination>("destination").get<Country>("country").get<Long>("countryId"),
                    countryId
                )
            }
        }

        filter.destinationId?.let { destinationId ->
            spec = spec.and { root, _, cb ->
                cb.equal(
                    root.get<Destination>("destination").get<Long>("id"),
                    destinationId
                )
            }
        }

        filter.categoryIds
            ?.takeIf { it.isNotEmpty() }
            ?.let { categoryIds ->
                spec = spec.and { root, query, _ ->
                    query.distinct(true)

                    root.join<SideQuest, Category>("categories")
                        .get<Long>("categoryId")
                        .`in`(categoryIds)
                }
            }

        filter.sourceTypeId?.let { sourceTypeId ->
            spec = spec.and { root, _, cb ->
                cb.equal(
                    root.get<SideQuestSource>("source").get<Long>("id"),
                    sourceTypeId
                )
            }
        }

        return sideQuestRepository.findAll(spec, pageable)
            .map { it.toResponse() }
            .toPageResponse()
    }
}