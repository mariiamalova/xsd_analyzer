package ru.homecredit.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.homecredit.dto.ThreeNode;
import ru.homecredit.dto.XsdElement;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreeBuilder {

    public static Optional<ThreeNode<XsdElement>> build(Map<XsdElement, List<XsdElement>> dependentList) {
        XsdElement root = dependentList.keySet().iterator().next();
        List<XsdElement> children = dependentList.get(root);

        ThreeNode<XsdElement> three = new ThreeNode<>(root);
        addChildren(children, three);

        for (Map.Entry<XsdElement, List<XsdElement>> entry : dependentList.entrySet()) {
            XsdElement currentRoot = entry.getKey();
            if (!currentRoot.equals(root)) {
                List<XsdElement> currentChildren = entry.getValue();
                passageOfAllDescendants(three, currentRoot, currentChildren);
            }
        }
        return Optional.of(three);
    }

    private static void passageOfAllDescendants(ThreeNode<XsdElement> three, XsdElement currentRoot,
                                                List<XsdElement> currentChildren) {
        for (ThreeNode<XsdElement> dependentElement : getDescendants(three)) {
            if (dependentElement.value().equals(currentRoot)) {
                addChildren(currentChildren, dependentElement);
            }
        }
    }

    private static void addChildren(List<XsdElement> children, ThreeNode<XsdElement> threeNode) {
        for (XsdElement child : children) {
            threeNode.addChild(new ThreeNode<>(child));
        }
    }

    public static List<ThreeNode<XsdElement>> getDescendants(ThreeNode<XsdElement> root) {
        List<ThreeNode<XsdElement>> nodes = new ArrayList<>();
        Deque<ThreeNode<XsdElement>> deque = new ArrayDeque<>();
        deque.push(root);
        while (!deque.isEmpty()) {
            ThreeNode<XsdElement> node = deque.pop();
            nodes.add(node);
            for (ThreeNode<XsdElement> child : node.children()) {
                deque.push(child);
            }
        }
        return nodes;
    }
}
