package org.nlpcn.es4sql.query;

import org.nlpcn.es4sql.exception.SqlParseException;

/**
 * Created by zy-xx on 2019/9/26.
 */
public interface Action {

    public SqlElasticRequestBuilder explain() throws SqlParseException;
}
