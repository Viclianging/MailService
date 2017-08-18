package com.ipinyou.data.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelFilerMaker {

	private static OutputStream ots;
	private static WritableWorkbook workbook;

	public static void createExcel(ResultSet result, int index, ApplicationProperties properties) {
		File file = new File(ApplicationProperties.getCacheFilePath().concat("/").concat(properties.getFileDir()));
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			if (workbook == null) {
				ots = new FileOutputStream(properties.getFileFullPath());
	        	workbook = Workbook.createWorkbook(ots);
			}

	        WritableSheet sheet = workbook.createSheet(properties.getSheetTitle(index), index);
	        ResultSetMetaData resmd = result.getMetaData();
	        int columnCount = resmd.getColumnCount();
	        int firstRowNum = 0;
		    if (StringUtils.isNotBlank(properties.getExcelTitles(index))) {
		        sheet.mergeCells(0, firstRowNum, columnCount - 1, 0);
		        WritableFont bold = new WritableFont(WritableFont.ARIAL, 18, WritableFont.BOLD);
		        WritableCellFormat titleFormate = new WritableCellFormat(bold);
		        titleFormate.setAlignment(jxl.format.Alignment.CENTRE);
		        titleFormate.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		        titleFormate.setBackground(Colour.RED);
		        Label title = new Label(0, 0, properties.getExcelTitles(index), titleFormate);
		        sheet.setRowView(firstRowNum++, 600, false);
		        sheet.addCell(title);
		    }

	        WritableFont color = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
	        color.setColour(Colour.BLACK);
	        WritableCellFormat textFormat = new WritableCellFormat(color);
	        textFormat.setAlignment(jxl.format.Alignment.CENTRE);
	        textFormat.setBackground(Colour.GRAY_25);
	        sheet.setRowView(firstRowNum, 400, false);
	        for(int colIndex = 1; colIndex <= columnCount; colIndex++) {
        		String columnName = resmd.getColumnName(colIndex);
    	        Label formate = new Label(colIndex - 1, firstRowNum, columnName, textFormat);
    	        sheet.addCell(formate);
        	}
	        WritableFont color2 = new WritableFont(WritableFont.ARIAL);
	        color2.setColour(Colour.BLACK);
	        WritableCellFormat contentFormat = new WritableCellFormat(color2);
	        CellView cellView = new CellView();
	        cellView.setAutosize(true);
	        while (result.next()) {
	        	for(int colIndex = 1; colIndex <= columnCount; colIndex++) {
	        		String val = String.valueOf(result.getObject(colIndex));
	        		sheet.setColumnView(colIndex - 1, cellView);
	        		Label label = new Label(colIndex - 1, result.getRow() + firstRowNum, val, contentFormat);
	    	        sheet.setRowView(result.getRow() + 1, 330, false);
	    	        sheet.addCell(label);
	        	}
	        }
		} catch(IOException e1) {
			e1.printStackTrace();
		} catch(WriteException e2) {
			e2.printStackTrace();
		} catch(SQLException e3) {
			e3.printStackTrace();
		}
    }

	public static void close() {
		try {
			workbook.write();
			workbook.close();
			ots.close();
			workbook = null;
			ots = null;
			System.gc();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
