package com.seraphim.loyverse.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.*;

@Slf4j
public class ItemVariantMapper {

    /**
     * Reads itemsFromReportLVApp.json from classpath and returns a map of item_name → list of variant_ids.
     */
    public static Map<String, List<String>> extractItemVariantMapFromResources() {
        Map<String, List<String>> itemVariantMap = new LinkedHashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            // Load the JSON file from the resources folder
            InputStream inputStream = ItemVariantMapper.class
                    .getClassLoader()
                    .getResourceAsStream("data/itemsFromReportLVApp.json");

            if (inputStream == null) {
                throw new IllegalArgumentException("File data/itemsFromReportLVApp.json not found in resources.");
            }

            JsonNode root = mapper.readTree(inputStream);
            JsonNode items = root.get("items");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    JsonNode itemNameNode = item.get("item_name");
                    if (itemNameNode == null) continue;

                    String itemName = itemNameNode.asText();
                    List<String> variantIds = new ArrayList<>();

                    JsonNode variants = item.get("variants");
                    if (variants != null && variants.isArray()) {
                        for (JsonNode variant : variants) {
                            JsonNode variantIdNode = variant.get("variant_id");
                            if (variantIdNode != null && !variantIdNode.isNull()) {
                                variantIds.add(variantIdNode.asText());
                            }
                        }
                    }

                    itemVariantMap.put(itemName, variantIds);
                }
            }

        } catch (Exception e) {
            log.error("Failed to read item variants from JSON file", e);
        }

        return itemVariantMap;
    }
}

