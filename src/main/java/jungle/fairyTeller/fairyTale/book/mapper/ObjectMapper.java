package jungle.fairyTeller.fairyTale.book.mapper;

import jungle.fairyTeller.fairyTale.book.dto.ObjectDTO;
import jungle.fairyTeller.fairyTale.book.entity.PageObjectEntity;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

//    public static List<ObjectDTO> convertToDTO(PageObjectEntity object) {
//        List<Map<String, Object>> objects = object.getObjects();
//        List<ObjectDTO> dtoList = new ArrayList<>();
//
//        for (Map<String, Object> obj : objects) {
//            ObjectDTO dto = new ObjectDTO();
//            dto.setType((String) obj.get("type"));
//            dto.setLeft((Integer) obj.get("left"));
//            dto.setTop((Integer) obj.get("top"));
//            dto.setWidth((Integer) obj.get("width"));
//            dto.setHeight((Integer) obj.get("height"));
//            dto.setRadius((Integer) obj.get("radius"));
//            dtoList.add(dto);
//        }
//
//        return dtoList;
//    }
}
