package com.rave.projectbabylonmaterials.client.tooltip.modern;

import java.util.List;

final class TooltipSectionMatcher {
    private TooltipSectionMatcher() {
    }

    static int find(List<String> lines, List<String> section) {
        if (section.isEmpty()) {
            return -1;
        }
        for (int start = 0; start + section.size() <= lines.size(); start++) {
            int offset = 0;
            while (offset < section.size() && section.get(offset).equals(lines.get(start + offset))) {
                offset++;
            }
            if (offset == section.size()) {
                return start;
            }
        }
        return -1;
    }
}
