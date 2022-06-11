package com.swp.backend.model;

import com.swp.backend.entity.SubYardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YardData extends YardModel {
    private List<SubYardModel> subYards;
}
