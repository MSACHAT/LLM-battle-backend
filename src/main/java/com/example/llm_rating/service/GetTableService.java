package com.example.llm_rating.service;

import com.example.llm_rating.DTO.ArenaTableItem;
import com.example.llm_rating.DTO.DataSource;
import com.example.llm_rating.model.Model;
import com.example.llm_rating.repository.ModelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GetTableService {

    private final CommunicationService communicationService;
    private final ModelRepository modelRepository;

    public DataSource processData(DataSource dataSource) {
        for (ArenaTableItem item : dataSource.getArenaTable()) {
            Optional<Model> modelInfo = modelRepository.findByModelName(item.getModel());
            modelInfo.ifPresent(model -> {
                item.setKnowledgeCutoff(model.getKnowledgeCutoff());
                item.setOrganization(model.getOrganization());
                item.setLicense(model.getLisence());
            });
        }

        return dataSource;

    }
}
