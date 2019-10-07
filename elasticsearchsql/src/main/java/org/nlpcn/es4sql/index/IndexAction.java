package org.nlpcn.es4sql.index;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.nlpcn.es4sql.Action;
import org.nlpcn.es4sql.domain.Insert;

/**
 * Created by zy-xx on 2019/9/27.
 */
public interface IndexAction extends Action {
    int getCount();
}
