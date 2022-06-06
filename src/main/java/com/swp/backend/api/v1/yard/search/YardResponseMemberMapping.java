package com.swp.backend.api.v1.yard.search;

import com.swp.backend.entity.YardEntity;
import com.swp.backend.service.YardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class YardResponseMemberMapping
{
    private YardService yardService;
    public YardResponseMember mapFromYardEntityToYardResponseMember(YardEntity yardEntity)
    {
        YardResponseMember yardResponseMember = new YardResponseMember();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        yardResponseMember.setAddress(yardEntity.getAddress());
        yardResponseMember.setId(yardEntity.getId());
        yardResponseMember.setDistrictId(yardEntity.getDistrictId());
        yardResponseMember.setName(yardEntity.getName());
        yardResponseMember.setOpenAt(yardEntity.getOpenAt().format(formatter));
        yardResponseMember.setCloseAt(yardEntity.getCloseAt().format(formatter));
        yardService.loadAllImages(yardResponseMember);
        yardResponseMember = yardService.loadAllImages(yardResponseMember);

        return yardResponseMember;
    }

    public List<YardResponseMember> mapFromListYardEntityToListYardResponse(List<YardEntity> yardEntities)
    {
        List<YardResponseMember> result = new ArrayList<YardResponseMember>();

        yardEntities.stream().forEach(yardEntity -> {
            YardResponseMember yardResponseMember = this.mapFromYardEntityToYardResponseMember(yardEntity);
            result.add(yardResponseMember);
        });

        return result;
    }
}
