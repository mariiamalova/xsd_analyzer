package ru.homecredit.dto;

import java.util.ArrayList;
import java.util.List;

public record ThreeNode<T>(T value, List<ThreeNode<T>> children) {
    public ThreeNode(T root) {
        this(root, new ArrayList<>());
    }

    public void addChild(ThreeNode<T> child) {
        children.add(child);
    }
}
