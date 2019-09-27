package org.nlpcn.es4sql;

import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.SqlElasticRequestBuilder;

/**
 * Created by zy-xx on 2019/9/26.
 */
public interface Action {

    public SqlElasticRequestBuilder explain() throws SqlParseException;
}
