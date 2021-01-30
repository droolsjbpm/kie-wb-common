/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./ContextExpression.css";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import {
  ContextEntries,
  ContextProps,
  DataType,
  ExpressionProps,
  LogicType,
  TableHandlerConfiguration,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryCell } from "./ContextEntryCell";
import * as _ from "lodash";
import { ContextEntry, DEFAULT_ENTRY_INFO_WIDTH } from "./ContextEntry";

const DEFAULT_CONTEXT_ENTRY_NAME = "ContextEntry-1";
const DEFAULT_CONTEXT_ENTRY_DATA_TYPE = DataType.Undefined;

export const ContextExpression: React.FunctionComponent<ContextProps> = ({
  name = DEFAULT_CONTEXT_ENTRY_NAME,
  dataType = DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
  onUpdatingNameAndDataType,
  contextEntries = [
    { name: DEFAULT_CONTEXT_ENTRY_NAME, dataType: DEFAULT_CONTEXT_ENTRY_DATA_TYPE, expression: {} } as DataRecord,
  ],
  result = {} as ExpressionProps,
  resultWidth,
  isHeadless = false,
  onUpdatingRecursiveExpression,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const handlerConfiguration: TableHandlerConfiguration = [
    {
      group: i18n.contextEntry,
      items: [
        { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
      ],
    },
  ];

  const [columns, setColumns] = useState([
    {
      label: name,
      accessor: name,
      dataType,
      disableHandlerOnHeader: true,
      disableResizing: true,
    },
  ]);

  const [rows, setRows] = useState(contextEntries);

  const [resultExpression, setResultExpression] = useState(result);

  const [resultEntryWidth, setResultEntryWidth] = useState(resultWidth);

  useEffect(() => {
    const expressionColumn = columns[0];
    const updatedDefinition: ContextProps = {
      logicType: LogicType.Context,
      name: expressionColumn.accessor,
      dataType: expressionColumn.dataType,
      contextEntries: rows as ContextEntries,
      result: resultExpression,
      ...(resultEntryWidth !== DEFAULT_ENTRY_INFO_WIDTH ? { resultWidth: resultEntryWidth } : {}),
    };
    isHeadless
      ? onUpdatingRecursiveExpression?.(updatedDefinition)
      : window.beeApi?.broadcastContextExpressionDefinition?.(updatedDefinition);
  }, [columns, isHeadless, onUpdatingRecursiveExpression, rows, resultExpression, resultEntryWidth]);

  /**
   * Every time the ContextExpression component gets re-rendered, automatically calculating table cells width to fit the entire content
   */
  useEffect(() => {
    document
      .querySelectorAll(".context-expression > .table-component > table > tbody > tr > td.data-cell")
      .forEach((td: HTMLElement) => (td.style.width = "100%"));
    document
      .querySelectorAll(".context-expression > .table-component > table > tbody > tr.table-row")
      .forEach((td: HTMLElement) => (td.style.width = "100%"));
    document
      .querySelectorAll(".context-expression > .table-component > table > thead > tr > th.resizable-column")
      .forEach((th: HTMLElement) => (th.style.width = "calc((100% - 60px)"));
  });

  const onUpdatingExpressionColumn = useCallback(
    ([expressionColumn]: [ColumnInstance]) => {
      onUpdatingNameAndDataType?.(expressionColumn.label, expressionColumn.dataType);
      setColumns(([prevExpressionColumn]) => [
        {
          ...prevExpressionColumn,
          label: expressionColumn.label,
          accessor: expressionColumn.accessor,
          dataType: expressionColumn.dataType,
          width: expressionColumn.width as number,
        },
      ]);
    },
    [onUpdatingNameAndDataType]
  );

  const generateNextAvailableEntryName: (lastIndex: number) => string = useCallback(
    (lastIndex) => {
      const candidateName = `ContextEntry-${lastIndex}`;
      const entryWithCandidateName = _.find(rows, { name: candidateName });
      return entryWithCandidateName ? generateNextAvailableEntryName(lastIndex + 1) : candidateName;
    },
    [rows]
  );

  const onRowAdding = useCallback(
    () => ({
      name: generateNextAvailableEntryName(rows.length),
      dataType: DataType.Undefined,
      expression: {},
    }),
    [generateNextAvailableEntryName, rows.length]
  );

  return (
    <div className="context-expression">
      <Table
        isHeadless={isHeadless}
        defaultCell={ContextEntryCell}
        columns={columns}
        rows={rows as DataRecord[]}
        onColumnsUpdate={onUpdatingExpressionColumn}
        onRowAdding={onRowAdding}
        onRowsUpdate={setRows}
        handlerConfiguration={handlerConfiguration}
      >
        <ContextEntry
          expression={resultExpression}
          onUpdatingRecursiveExpression={setResultExpression}
          width={resultWidth}
          onUpdatingWidth={setResultEntryWidth}
        >
          <div className="context-result">{`<result>`}</div>
        </ContextEntry>
      </Table>
    </div>
  );
};
