/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Modified BSD License
// (the "License"); you may not use this file except in compliance with
// the License. You may obtain a copy of the License at:
//
// http://opensource.org/licenses/BSD-3-Clause
*/
package sqlline;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

/**
 * Abstract base that implements {@link DatabaseMetaData} by delegating every
 * method to a wrapped instance. Lets tests subclass and override just the
 * methods they care about, without requiring access to the driver's own
 * metadata class (some drivers seal their JDBC package).
 */
abstract class DelegatingDatabaseMetaData implements DatabaseMetaData {
  private final DatabaseMetaData delegate;

  DelegatingDatabaseMetaData(DatabaseMetaData delegate) {
    this.delegate = delegate;
  }

  @Override public <T> T unwrap(Class<T> iface) throws SQLException {
    return delegate.unwrap(iface);
  }

  @Override public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return delegate.isWrapperFor(iface);
  }

  @Override public boolean allProceduresAreCallable() throws SQLException {
    return delegate.allProceduresAreCallable();
  }

  @Override public boolean allTablesAreSelectable() throws SQLException {
    return delegate.allTablesAreSelectable();
  }

  @Override public String getURL() throws SQLException {
    return delegate.getURL();
  }

  @Override public String getUserName() throws SQLException {
    return delegate.getUserName();
  }

  @Override public boolean isReadOnly() throws SQLException {
    return delegate.isReadOnly();
  }

  @Override public boolean nullsAreSortedHigh() throws SQLException {
    return delegate.nullsAreSortedHigh();
  }

  @Override public boolean nullsAreSortedLow() throws SQLException {
    return delegate.nullsAreSortedLow();
  }

  @Override public boolean nullsAreSortedAtStart() throws SQLException {
    return delegate.nullsAreSortedAtStart();
  }

  @Override public boolean nullsAreSortedAtEnd() throws SQLException {
    return delegate.nullsAreSortedAtEnd();
  }

  @Override public String getDatabaseProductName() throws SQLException {
    return delegate.getDatabaseProductName();
  }

  @Override public String getDatabaseProductVersion() throws SQLException {
    return delegate.getDatabaseProductVersion();
  }

  @Override public String getDriverName() throws SQLException {
    return delegate.getDriverName();
  }

  @Override public String getDriverVersion() throws SQLException {
    return delegate.getDriverVersion();
  }

  @Override public int getDriverMajorVersion() {
    return delegate.getDriverMajorVersion();
  }

  @Override public int getDriverMinorVersion() {
    return delegate.getDriverMinorVersion();
  }

  @Override public boolean usesLocalFiles() throws SQLException {
    return delegate.usesLocalFiles();
  }

  @Override public boolean usesLocalFilePerTable() throws SQLException {
    return delegate.usesLocalFilePerTable();
  }

  @Override public boolean supportsMixedCaseIdentifiers() throws SQLException {
    return delegate.supportsMixedCaseIdentifiers();
  }

  @Override public boolean storesUpperCaseIdentifiers() throws SQLException {
    return delegate.storesUpperCaseIdentifiers();
  }

  @Override public boolean storesLowerCaseIdentifiers() throws SQLException {
    return delegate.storesLowerCaseIdentifiers();
  }

  @Override public boolean storesMixedCaseIdentifiers() throws SQLException {
    return delegate.storesMixedCaseIdentifiers();
  }

  @Override public boolean
      supportsMixedCaseQuotedIdentifiers()
         throws SQLException {
    return delegate.supportsMixedCaseQuotedIdentifiers();
  }

  @Override public boolean
      storesUpperCaseQuotedIdentifiers()
         throws SQLException {
    return delegate.storesUpperCaseQuotedIdentifiers();
  }

  @Override public boolean
      storesLowerCaseQuotedIdentifiers()
         throws SQLException {
    return delegate.storesLowerCaseQuotedIdentifiers();
  }

  @Override public boolean
      storesMixedCaseQuotedIdentifiers()
         throws SQLException {
    return delegate.storesMixedCaseQuotedIdentifiers();
  }

  @Override public String getIdentifierQuoteString() throws SQLException {
    return delegate.getIdentifierQuoteString();
  }

  @Override public String getSQLKeywords() throws SQLException {
    return delegate.getSQLKeywords();
  }

  @Override public String getNumericFunctions() throws SQLException {
    return delegate.getNumericFunctions();
  }

  @Override public String getStringFunctions() throws SQLException {
    return delegate.getStringFunctions();
  }

  @Override public String getSystemFunctions() throws SQLException {
    return delegate.getSystemFunctions();
  }

  @Override public String getTimeDateFunctions() throws SQLException {
    return delegate.getTimeDateFunctions();
  }

  @Override public String getSearchStringEscape() throws SQLException {
    return delegate.getSearchStringEscape();
  }

  @Override public String getExtraNameCharacters() throws SQLException {
    return delegate.getExtraNameCharacters();
  }

  @Override public boolean
      supportsAlterTableWithAddColumn()
         throws SQLException {
    return delegate.supportsAlterTableWithAddColumn();
  }

  @Override public boolean
      supportsAlterTableWithDropColumn()
         throws SQLException {
    return delegate.supportsAlterTableWithDropColumn();
  }

  @Override public boolean supportsColumnAliasing() throws SQLException {
    return delegate.supportsColumnAliasing();
  }

  @Override public boolean nullPlusNonNullIsNull() throws SQLException {
    return delegate.nullPlusNonNullIsNull();
  }

  @Override public boolean supportsConvert() throws SQLException {
    return delegate.supportsConvert();
  }

  @Override public boolean supportsConvert(int a1, int a2) throws SQLException {
    return delegate.supportsConvert(a1, a2);
  }

  @Override public boolean supportsTableCorrelationNames() throws SQLException {
    return delegate.supportsTableCorrelationNames();
  }

  @Override public boolean
      supportsDifferentTableCorrelationNames()
         throws SQLException {
    return delegate.supportsDifferentTableCorrelationNames();
  }

  @Override public boolean supportsExpressionsInOrderBy() throws SQLException {
    return delegate.supportsExpressionsInOrderBy();
  }

  @Override public boolean supportsOrderByUnrelated() throws SQLException {
    return delegate.supportsOrderByUnrelated();
  }

  @Override public boolean supportsGroupBy() throws SQLException {
    return delegate.supportsGroupBy();
  }

  @Override public boolean supportsGroupByUnrelated() throws SQLException {
    return delegate.supportsGroupByUnrelated();
  }

  @Override public boolean supportsGroupByBeyondSelect() throws SQLException {
    return delegate.supportsGroupByBeyondSelect();
  }

  @Override public boolean supportsLikeEscapeClause() throws SQLException {
    return delegate.supportsLikeEscapeClause();
  }

  @Override public boolean supportsMultipleResultSets() throws SQLException {
    return delegate.supportsMultipleResultSets();
  }

  @Override public boolean supportsMultipleTransactions() throws SQLException {
    return delegate.supportsMultipleTransactions();
  }

  @Override public boolean supportsNonNullableColumns() throws SQLException {
    return delegate.supportsNonNullableColumns();
  }

  @Override public boolean supportsMinimumSQLGrammar() throws SQLException {
    return delegate.supportsMinimumSQLGrammar();
  }

  @Override public boolean supportsCoreSQLGrammar() throws SQLException {
    return delegate.supportsCoreSQLGrammar();
  }

  @Override public boolean supportsExtendedSQLGrammar() throws SQLException {
    return delegate.supportsExtendedSQLGrammar();
  }

  @Override public boolean supportsANSI92EntryLevelSQL() throws SQLException {
    return delegate.supportsANSI92EntryLevelSQL();
  }

  @Override public boolean supportsANSI92IntermediateSQL() throws SQLException {
    return delegate.supportsANSI92IntermediateSQL();
  }

  @Override public boolean supportsANSI92FullSQL() throws SQLException {
    return delegate.supportsANSI92FullSQL();
  }

  @Override public boolean
      supportsIntegrityEnhancementFacility()
         throws SQLException {
    return delegate.supportsIntegrityEnhancementFacility();
  }

  @Override public boolean supportsOuterJoins() throws SQLException {
    return delegate.supportsOuterJoins();
  }

  @Override public boolean supportsFullOuterJoins() throws SQLException {
    return delegate.supportsFullOuterJoins();
  }

  @Override public boolean supportsLimitedOuterJoins() throws SQLException {
    return delegate.supportsLimitedOuterJoins();
  }

  @Override public String getSchemaTerm() throws SQLException {
    return delegate.getSchemaTerm();
  }

  @Override public String getProcedureTerm() throws SQLException {
    return delegate.getProcedureTerm();
  }

  @Override public String getCatalogTerm() throws SQLException {
    return delegate.getCatalogTerm();
  }

  @Override public boolean isCatalogAtStart() throws SQLException {
    return delegate.isCatalogAtStart();
  }

  @Override public String getCatalogSeparator() throws SQLException {
    return delegate.getCatalogSeparator();
  }

  @Override public boolean
      supportsSchemasInDataManipulation()
         throws SQLException {
    return delegate.supportsSchemasInDataManipulation();
  }

  @Override public boolean
      supportsSchemasInProcedureCalls()
         throws SQLException {
    return delegate.supportsSchemasInProcedureCalls();
  }

  @Override public boolean
      supportsSchemasInTableDefinitions()
         throws SQLException {
    return delegate.supportsSchemasInTableDefinitions();
  }

  @Override public boolean
      supportsSchemasInIndexDefinitions()
         throws SQLException {
    return delegate.supportsSchemasInIndexDefinitions();
  }

  @Override public boolean
      supportsSchemasInPrivilegeDefinitions()
         throws SQLException {
    return delegate.supportsSchemasInPrivilegeDefinitions();
  }

  @Override public boolean
      supportsCatalogsInDataManipulation()
         throws SQLException {
    return delegate.supportsCatalogsInDataManipulation();
  }

  @Override public boolean
      supportsCatalogsInProcedureCalls()
         throws SQLException {
    return delegate.supportsCatalogsInProcedureCalls();
  }

  @Override public boolean
      supportsCatalogsInTableDefinitions()
         throws SQLException {
    return delegate.supportsCatalogsInTableDefinitions();
  }

  @Override public boolean
      supportsCatalogsInIndexDefinitions()
         throws SQLException {
    return delegate.supportsCatalogsInIndexDefinitions();
  }

  @Override public boolean
      supportsCatalogsInPrivilegeDefinitions()
         throws SQLException {
    return delegate.supportsCatalogsInPrivilegeDefinitions();
  }

  @Override public boolean supportsPositionedDelete() throws SQLException {
    return delegate.supportsPositionedDelete();
  }

  @Override public boolean supportsPositionedUpdate() throws SQLException {
    return delegate.supportsPositionedUpdate();
  }

  @Override public boolean supportsSelectForUpdate() throws SQLException {
    return delegate.supportsSelectForUpdate();
  }

  @Override public boolean supportsStoredProcedures() throws SQLException {
    return delegate.supportsStoredProcedures();
  }

  @Override public boolean
      supportsSubqueriesInComparisons()
         throws SQLException {
    return delegate.supportsSubqueriesInComparisons();
  }

  @Override public boolean supportsSubqueriesInExists() throws SQLException {
    return delegate.supportsSubqueriesInExists();
  }

  @Override public boolean supportsSubqueriesInIns() throws SQLException {
    return delegate.supportsSubqueriesInIns();
  }

  @Override public boolean
      supportsSubqueriesInQuantifieds()
         throws SQLException {
    return delegate.supportsSubqueriesInQuantifieds();
  }

  @Override public boolean supportsCorrelatedSubqueries() throws SQLException {
    return delegate.supportsCorrelatedSubqueries();
  }

  @Override public boolean supportsUnion() throws SQLException {
    return delegate.supportsUnion();
  }

  @Override public boolean supportsUnionAll() throws SQLException {
    return delegate.supportsUnionAll();
  }

  @Override public boolean
      supportsOpenCursorsAcrossCommit()
         throws SQLException {
    return delegate.supportsOpenCursorsAcrossCommit();
  }

  @Override public boolean
      supportsOpenCursorsAcrossRollback()
         throws SQLException {
    return delegate.supportsOpenCursorsAcrossRollback();
  }

  @Override public boolean
      supportsOpenStatementsAcrossCommit()
         throws SQLException {
    return delegate.supportsOpenStatementsAcrossCommit();
  }

  @Override public boolean
      supportsOpenStatementsAcrossRollback()
         throws SQLException {
    return delegate.supportsOpenStatementsAcrossRollback();
  }

  @Override public int getMaxBinaryLiteralLength() throws SQLException {
    return delegate.getMaxBinaryLiteralLength();
  }

  @Override public int getMaxCharLiteralLength() throws SQLException {
    return delegate.getMaxCharLiteralLength();
  }

  @Override public int getMaxColumnNameLength() throws SQLException {
    return delegate.getMaxColumnNameLength();
  }

  @Override public int getMaxColumnsInGroupBy() throws SQLException {
    return delegate.getMaxColumnsInGroupBy();
  }

  @Override public int getMaxColumnsInIndex() throws SQLException {
    return delegate.getMaxColumnsInIndex();
  }

  @Override public int getMaxColumnsInOrderBy() throws SQLException {
    return delegate.getMaxColumnsInOrderBy();
  }

  @Override public int getMaxColumnsInSelect() throws SQLException {
    return delegate.getMaxColumnsInSelect();
  }

  @Override public int getMaxColumnsInTable() throws SQLException {
    return delegate.getMaxColumnsInTable();
  }

  @Override public int getMaxConnections() throws SQLException {
    return delegate.getMaxConnections();
  }

  @Override public int getMaxCursorNameLength() throws SQLException {
    return delegate.getMaxCursorNameLength();
  }

  @Override public int getMaxIndexLength() throws SQLException {
    return delegate.getMaxIndexLength();
  }

  @Override public int getMaxSchemaNameLength() throws SQLException {
    return delegate.getMaxSchemaNameLength();
  }

  @Override public int getMaxProcedureNameLength() throws SQLException {
    return delegate.getMaxProcedureNameLength();
  }

  @Override public int getMaxCatalogNameLength() throws SQLException {
    return delegate.getMaxCatalogNameLength();
  }

  @Override public int getMaxRowSize() throws SQLException {
    return delegate.getMaxRowSize();
  }

  @Override public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
    return delegate.doesMaxRowSizeIncludeBlobs();
  }

  @Override public int getMaxStatementLength() throws SQLException {
    return delegate.getMaxStatementLength();
  }

  @Override public int getMaxStatements() throws SQLException {
    return delegate.getMaxStatements();
  }

  @Override public int getMaxTableNameLength() throws SQLException {
    return delegate.getMaxTableNameLength();
  }

  @Override public int getMaxTablesInSelect() throws SQLException {
    return delegate.getMaxTablesInSelect();
  }

  @Override public int getMaxUserNameLength() throws SQLException {
    return delegate.getMaxUserNameLength();
  }

  @Override public int getDefaultTransactionIsolation() throws SQLException {
    return delegate.getDefaultTransactionIsolation();
  }

  @Override public boolean supportsTransactions() throws SQLException {
    return delegate.supportsTransactions();
  }

  @Override public boolean supportsTransactionIsolationLevel(
      int a1) throws SQLException {
    return delegate.supportsTransactionIsolationLevel(a1);
  }

  @Override public boolean
      supportsDataDefinitionAndDataManipulationTransactions()
         throws SQLException {
    return delegate.supportsDataDefinitionAndDataManipulationTransactions();
  }

  @Override public boolean
      supportsDataManipulationTransactionsOnly()
         throws SQLException {
    return delegate.supportsDataManipulationTransactionsOnly();
  }

  @Override public boolean
      dataDefinitionCausesTransactionCommit()
         throws SQLException {
    return delegate.dataDefinitionCausesTransactionCommit();
  }

  @Override public boolean
      dataDefinitionIgnoredInTransactions()
         throws SQLException {
    return delegate.dataDefinitionIgnoredInTransactions();
  }

  @Override public ResultSet getProcedures(
      String a1,
      String a2,
      String a3) throws SQLException {
    return delegate.getProcedures(a1, a2, a3);
  }

  @Override public ResultSet getProcedureColumns(
      String a1,
      String a2,
      String a3,
      String a4) throws SQLException {
    return delegate.getProcedureColumns(a1, a2, a3, a4);
  }

  @Override public ResultSet getTables(
      String a1,
      String a2,
      String a3,
      String[] a4) throws SQLException {
    return delegate.getTables(a1, a2, a3, a4);
  }

  @Override public ResultSet getSchemas() throws SQLException {
    return delegate.getSchemas();
  }

  @Override public ResultSet getCatalogs() throws SQLException {
    return delegate.getCatalogs();
  }

  @Override public ResultSet getTableTypes() throws SQLException {
    return delegate.getTableTypes();
  }

  @Override public ResultSet getColumns(
      String a1,
      String a2,
      String a3,
      String a4) throws SQLException {
    return delegate.getColumns(a1, a2, a3, a4);
  }

  @Override public ResultSet getColumnPrivileges(
      String a1,
      String a2,
      String a3,
      String a4) throws SQLException {
    return delegate.getColumnPrivileges(a1, a2, a3, a4);
  }

  @Override public ResultSet getTablePrivileges(
      String a1,
      String a2,
      String a3) throws SQLException {
    return delegate.getTablePrivileges(a1, a2, a3);
  }

  @Override public ResultSet getBestRowIdentifier(
      String a1,
      String a2,
      String a3,
      int a4,
      boolean a5) throws SQLException {
    return delegate.getBestRowIdentifier(a1, a2, a3, a4, a5);
  }

  @Override public ResultSet getVersionColumns(
      String a1,
      String a2,
      String a3) throws SQLException {
    return delegate.getVersionColumns(a1, a2, a3);
  }

  @Override public ResultSet getPrimaryKeys(
      String a1,
      String a2,
      String a3) throws SQLException {
    return delegate.getPrimaryKeys(a1, a2, a3);
  }

  @Override public ResultSet getImportedKeys(
      String a1,
      String a2,
      String a3) throws SQLException {
    return delegate.getImportedKeys(a1, a2, a3);
  }

  @Override public ResultSet getExportedKeys(
      String a1,
      String a2,
      String a3) throws SQLException {
    return delegate.getExportedKeys(a1, a2, a3);
  }

  @Override public ResultSet getCrossReference(
      String a1,
      String a2,
      String a3,
      String a4,
      String a5,
      String a6) throws SQLException {
    return delegate.getCrossReference(a1, a2, a3, a4, a5, a6);
  }

  @Override public ResultSet getTypeInfo() throws SQLException {
    return delegate.getTypeInfo();
  }

  @Override public ResultSet getIndexInfo(
      String a1,
      String a2,
      String a3,
      boolean a4,
      boolean a5) throws SQLException {
    return delegate.getIndexInfo(a1, a2, a3, a4, a5);
  }

  @Override public boolean supportsResultSetType(int a1) throws SQLException {
    return delegate.supportsResultSetType(a1);
  }

  @Override public boolean supportsResultSetConcurrency(
      int a1,
      int a2) throws SQLException {
    return delegate.supportsResultSetConcurrency(a1, a2);
  }

  @Override public boolean ownUpdatesAreVisible(int a1) throws SQLException {
    return delegate.ownUpdatesAreVisible(a1);
  }

  @Override public boolean ownDeletesAreVisible(int a1) throws SQLException {
    return delegate.ownDeletesAreVisible(a1);
  }

  @Override public boolean ownInsertsAreVisible(int a1) throws SQLException {
    return delegate.ownInsertsAreVisible(a1);
  }

  @Override public boolean othersUpdatesAreVisible(int a1) throws SQLException {
    return delegate.othersUpdatesAreVisible(a1);
  }

  @Override public boolean othersDeletesAreVisible(int a1) throws SQLException {
    return delegate.othersDeletesAreVisible(a1);
  }

  @Override public boolean othersInsertsAreVisible(int a1) throws SQLException {
    return delegate.othersInsertsAreVisible(a1);
  }

  @Override public boolean updatesAreDetected(int a1) throws SQLException {
    return delegate.updatesAreDetected(a1);
  }

  @Override public boolean deletesAreDetected(int a1) throws SQLException {
    return delegate.deletesAreDetected(a1);
  }

  @Override public boolean insertsAreDetected(int a1) throws SQLException {
    return delegate.insertsAreDetected(a1);
  }

  @Override public boolean supportsBatchUpdates() throws SQLException {
    return delegate.supportsBatchUpdates();
  }

  @Override public ResultSet getUDTs(
      String a1,
      String a2,
      String a3,
      int[] a4) throws SQLException {
    return delegate.getUDTs(a1, a2, a3, a4);
  }

  @Override public Connection getConnection() throws SQLException {
    return delegate.getConnection();
  }

  @Override public boolean supportsSavepoints() throws SQLException {
    return delegate.supportsSavepoints();
  }

  @Override public boolean supportsNamedParameters() throws SQLException {
    return delegate.supportsNamedParameters();
  }

  @Override public boolean supportsMultipleOpenResults() throws SQLException {
    return delegate.supportsMultipleOpenResults();
  }

  @Override public boolean supportsGetGeneratedKeys() throws SQLException {
    return delegate.supportsGetGeneratedKeys();
  }

  @Override public ResultSet getSuperTypes(
      String a1,
      String a2,
      String a3) throws SQLException {
    return delegate.getSuperTypes(a1, a2, a3);
  }

  @Override public ResultSet getSuperTables(
      String a1,
      String a2,
      String a3) throws SQLException {
    return delegate.getSuperTables(a1, a2, a3);
  }

  @Override public ResultSet getAttributes(
      String a1,
      String a2,
      String a3,
      String a4) throws SQLException {
    return delegate.getAttributes(a1, a2, a3, a4);
  }

  @Override public boolean supportsResultSetHoldability(
      int a1) throws SQLException {
    return delegate.supportsResultSetHoldability(a1);
  }

  @Override public int getResultSetHoldability() throws SQLException {
    return delegate.getResultSetHoldability();
  }

  @Override public int getDatabaseMajorVersion() throws SQLException {
    return delegate.getDatabaseMajorVersion();
  }

  @Override public int getDatabaseMinorVersion() throws SQLException {
    return delegate.getDatabaseMinorVersion();
  }

  @Override public int getJDBCMajorVersion() throws SQLException {
    return delegate.getJDBCMajorVersion();
  }

  @Override public int getJDBCMinorVersion() throws SQLException {
    return delegate.getJDBCMinorVersion();
  }

  @Override public int getSQLStateType() throws SQLException {
    return delegate.getSQLStateType();
  }

  @Override public boolean locatorsUpdateCopy() throws SQLException {
    return delegate.locatorsUpdateCopy();
  }

  @Override public boolean supportsStatementPooling() throws SQLException {
    return delegate.supportsStatementPooling();
  }

  @Override public RowIdLifetime getRowIdLifetime() throws SQLException {
    return delegate.getRowIdLifetime();
  }

  @Override public ResultSet getSchemas(
      String a1,
      String a2) throws SQLException {
    return delegate.getSchemas(a1, a2);
  }

  @Override public boolean
      supportsStoredFunctionsUsingCallSyntax()
         throws SQLException {
    return delegate.supportsStoredFunctionsUsingCallSyntax();
  }

  @Override public boolean
      autoCommitFailureClosesAllResultSets()
         throws SQLException {
    return delegate.autoCommitFailureClosesAllResultSets();
  }

  @Override public ResultSet getClientInfoProperties() throws SQLException {
    return delegate.getClientInfoProperties();
  }

  @Override public ResultSet getFunctions(
      String a1,
      String a2,
      String a3) throws SQLException {
    return delegate.getFunctions(a1, a2, a3);
  }

  @Override public ResultSet getFunctionColumns(
      String a1,
      String a2,
      String a3,
      String a4) throws SQLException {
    return delegate.getFunctionColumns(a1, a2, a3, a4);
  }

  @Override public ResultSet getPseudoColumns(
      String a1,
      String a2,
      String a3,
      String a4) throws SQLException {
    return delegate.getPseudoColumns(a1, a2, a3, a4);
  }

  @Override public boolean generatedKeyAlwaysReturned() throws SQLException {
    return delegate.generatedKeyAlwaysReturned();
  }

}

// End DelegatingDatabaseMetaData.java
