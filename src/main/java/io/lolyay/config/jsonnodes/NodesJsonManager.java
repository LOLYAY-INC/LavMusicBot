package io.lolyay.config.jsonnodes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.lolyay.config.ConfigManager;
import io.lolyay.utils.Logger;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class NodesJsonManager {
    private static File getNodesFile() {
        String nodesFilePath = ConfigManager.getConfig("nodes-json-file");
        File file = new File(nodesFilePath);
        if (!file.exists()) {
            Logger.err("nodes.json file not found at path: " + nodesFilePath);
            System.exit(1);
        }
        return file;
    }


    public static ArrayList<JsonNode> loadNodes() {
        // read nodes.json file
        String json;
        try {
            json = new String(Files.readAllBytes(getNodesFile().toPath()));
            if (json.isBlank()) {
                Logger.err("nodes.json file is empty.");
                System.exit(1);
            }
            ArrayList<JsonNode> jNodeList = parseNodes(json);
            if (jNodeList.isEmpty()) {
                Logger.err("nodes.json file is empty.");
                System.exit(1);
            }

            Logger.debug("Loaded " + jNodeList.size() + " nodes from nodes.json.");
            return jNodeList;
        } catch (Exception e) {
            Logger.err("Error loading nodes.json file: " + e.getMessage());
            System.exit(1);
        }
        return null;

    }

    private static ArrayList<JsonNode> parseNodes(String json) throws Exception {
        Type nodeListType = new TypeToken<List<JsonNode>>() {}.getType();
        return new Gson().fromJson(json, nodeListType);
    }


    private static String getConProtocol(JsonNode node) {
        return node.secure() ? "wss://" : "ws://";
    }
}
