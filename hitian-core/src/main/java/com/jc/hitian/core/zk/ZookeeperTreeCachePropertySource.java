package com.jc.hitian.core.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.springframework.context.Lifecycle;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Spencer Gibb
 */
@Slf4j
public class ZookeeperTreeCachePropertySource extends AbstractZookeeperPropertySource
        implements Lifecycle {

    private TreeCache cache;
    private boolean running;

    public ZookeeperTreeCachePropertySource(String context, CuratorFramework source) {
        super(context, source);
    }

    @Override
    public void start() {
        try {
            cache = TreeCache.newBuilder(source, this.getContext()).build();
            cache.start();
            running = true;
            // no race condition since ZookeeperAutoConfiguration.curatorFramework
            // calls curator.blockUntilConnected
        } catch (NoNodeException e) {
            // no node, ignore
        } catch (Exception e) {
            log.error("Error initializing ZookeeperPropertySource", e);
        }
    }

    @Override
    public Object getProperty(String name) {
        String fullPath = this.getContext() + "/" + name.replace(".", "/");
        byte[] bytes = null;
        ChildData data = cache.getCurrentData(fullPath);
        if (data != null) {
            bytes = data.getData();
        }
        if (bytes == null)
            return null;
        return new String(bytes, Charset.forName("UTF-8"));
    }

    @Override
    public String[] getPropertyNames() {
        List<String> keys = new ArrayList<>();
        findKeys(keys, this.getContext());
        return keys.toArray(new String[0]);
    }

    protected void findKeys(List<String> keys, String path) {
        log.trace("enter findKeysCached for path: " + path);
        Map<String, ChildData> children = cache.getCurrentChildren(path);

        if (children == null)
            return;
        for (Map.Entry<String, ChildData> entry : children.entrySet()) {
            ChildData child = entry.getValue();
            if (child.getData() == null || child.getData().length == 0) {
                findKeys(keys, child.getPath());
            } else {
                keys.add(sanitizeKey(child.getPath()));
            }
        }
        log.trace("leaving findKeysCached for path: " + path);
    }


    @Override
    public void stop() {
        cache.close();
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

}
