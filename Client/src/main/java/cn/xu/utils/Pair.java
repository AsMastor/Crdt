package cn.xu.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair <K, V>{
    K k;
    V v;
}
