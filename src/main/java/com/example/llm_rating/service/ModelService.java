package com.example.llm_rating.service;



import com.example.llm_rating.model.Model;
import com.example.llm_rating.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModelService {

    private final ModelRepository modelRepository;

    @Autowired
    public ModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }
    public Model getModelById(String id) {
        System.out.println("长在运行getmodelbyid");
        Model data = modelRepository.findById(id).orElse(null);
        if (data==null){
            System.out.println("model为null");
            return null;
        }
        System.out.println(data.getModelName());
        return data;
    }

    public Model getModelByModelName(String ModelName) {
        System.out.println("长在运行getmodelbyid");
        Model data = modelRepository.findByModelName(ModelName).orElse(null);
        if (data==null){
            System.out.println("model为null");
            return null;
        }
        System.out.println(data.getId());
        return data;
    }
}