package com.sixestates.type.faas;

/**
 * @author yec
 * @description
 * @Data 2026/1/5
 */
public final class HistoryQueryParamsBuilder {
    private Integer page;
    private Integer limit;
    private String sortColumn;
    private String sortOrder;
    private Integer taskStatus;
    private Integer status;
    private String fileTypeCode;
    private Integer source;
    private Boolean edited;
    private Boolean hitl;
    private String fileName;
    private String startCreateTime;
    private String endCreateTime;

    private HistoryQueryParamsBuilder() {
    }

    public static HistoryQueryParamsBuilder aHistoryQueryParams() {
        return new HistoryQueryParamsBuilder();
    }

    public HistoryQueryParamsBuilder withPage(Integer page) {
        this.page = page;
        return this;
    }

    public HistoryQueryParamsBuilder withLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public HistoryQueryParamsBuilder withSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
        return this;
    }

    public HistoryQueryParamsBuilder withSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public HistoryQueryParamsBuilder withTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
        return this;
    }

    public HistoryQueryParamsBuilder withStatus(Integer status) {
        this.status = status;
        return this;
    }

    public HistoryQueryParamsBuilder withFileTypeCode(String fileTypeCode) {
        this.fileTypeCode = fileTypeCode;
        return this;
    }

    public HistoryQueryParamsBuilder withSource(Integer source) {
        this.source = source;
        return this;
    }

    public HistoryQueryParamsBuilder withEdited(Boolean edited) {
        this.edited = edited;
        return this;
    }

    public HistoryQueryParamsBuilder withHitl(Boolean hitl) {
        this.hitl = hitl;
        return this;
    }

    public HistoryQueryParamsBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public HistoryQueryParamsBuilder withStartCreateTime(String startCreateTime) {
        this.startCreateTime = startCreateTime;
        return this;
    }

    public HistoryQueryParamsBuilder withEndCreateTime(String endCreateTime) {
        this.endCreateTime = endCreateTime;
        return this;
    }

    public HistoryQueryParams build() {
        HistoryQueryParams historyQueryParams = new HistoryQueryParams();
        historyQueryParams.setPage(page);
        historyQueryParams.setLimit(limit);
        historyQueryParams.setSortColumn(sortColumn);
        historyQueryParams.setSortOrder(sortOrder);
        historyQueryParams.setTaskStatus(taskStatus);
        historyQueryParams.setStatus(status);
        historyQueryParams.setFileTypeCode(fileTypeCode);
        historyQueryParams.setSource(source);
        historyQueryParams.setEdited(edited);
        historyQueryParams.setHitl(hitl);
        historyQueryParams.setFileName(fileName);
        historyQueryParams.setStartCreateTime(startCreateTime);
        historyQueryParams.setEndCreateTime(endCreateTime);
        return historyQueryParams;
    }
}
