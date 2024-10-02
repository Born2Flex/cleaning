package ua.edu.ukma.cleaning.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceWithType {
    private Resource resource;
    private String mediaType;
}
