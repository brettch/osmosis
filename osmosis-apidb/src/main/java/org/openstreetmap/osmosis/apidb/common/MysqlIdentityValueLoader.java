// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.apidb.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.database.ReleasableStatementContainer;

/**
 * Mysql implementation of an identity value loader.
 *
 * @author Brett Henderson
 */
public class MysqlIdentityValueLoader implements IdentityValueLoader {
    private static final String SQL_SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID() AS lastInsertId FROM DUAL";

    private DatabaseContext dbCtx;
    private ReleasableStatementContainer statementContainer;
    private PreparedStatement selectInsertIdStatement;

    /**
     * Creates a new instance.
     *
     * @param dbCtx
     *            The database context to use for all database access.
     */
    public MysqlIdentityValueLoader(DatabaseContext dbCtx) {
        this.dbCtx = dbCtx;

        statementContainer = new ReleasableStatementContainer();
    }

    /**
     * Returns the id of the most recently inserted row on the current
     * connection.
     *
     * @return The newly inserted id.
     */
    public long getLastInsertId() {
        if (selectInsertIdStatement == null) {
            selectInsertIdStatement =
                    statementContainer.add(dbCtx.prepareStatementForStreaming(SQL_SELECT_LAST_INSERT_ID));
        }

        try (ResultSet lastInsertQuery = selectInsertIdStatement.executeQuery()) {
            lastInsertQuery.next();
            return lastInsertQuery.getLong("lastInsertId");
        } catch (SQLException e) {
            throw new OsmosisRuntimeException("Unable to retrieve the id of the newly inserted record.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastSequenceId(String sequenceName) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        statementContainer.close();
    }
}
