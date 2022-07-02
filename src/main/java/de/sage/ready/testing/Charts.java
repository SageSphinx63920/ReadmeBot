package de.sage.ready.testing;

import java.util.Arrays;

import com.hyjavacharts.chart.Highchart;
import com.hyjavacharts.model.common.Color;
import com.hyjavacharts.model.highcharts.ChartOptions;
import com.hyjavacharts.model.highcharts.constants.ChartType;
import com.hyjavacharts.model.highcharts.constants.Cursor;
import com.hyjavacharts.model.highcharts.series.SeriesPie;
import com.hyjavacharts.model.highcharts.series.seriespie.Data;

public class Charts {
    private static final long serialVersionUID = 1L;

    public static void main(String[] args){

    }

    public static Highchart configure() {
        Highchart highChart = new Highchart();
        ChartOptions chartOptions = highChart.getChartOptions();

        chartOptions.getChart().setType(ChartType.PIE);
        chartOptions.getChart().setPlotBackgroundColor(null).setPlotBorderWidth(null).setPlotShadow(false);
        chartOptions.getTitle().setText("Air Composition").setY(225);

        chartOptions.setColors(Arrays.asList(new Color("#01BAF2"), new Color("#71BF45"), new Color("#FAA74B")));

        chartOptions.getLegend().setEnabled(false);
        chartOptions.getTooltip().setPointFormat("{series.name}: <b>{point.percentage:.1f}%</b>");

        chartOptions.getPlotOptions().getPie()
                .setAllowPointSelect(true)
                .setCursor(Cursor.POINTER)
                .setShowInLegend(true)
                .getDataLabels().setEnabled(true).getFormatter().setFunctionBody("return this.key+ ': ' + this.y + '%';");

        SeriesPie seriesPie = new SeriesPie();
        seriesPie.setName("Composition").setColorByPoint(true).setInnerSize("70%");
        seriesPie.setDataAsArrayObject(Arrays.asList(
                new Data().setName("Nitrogen").setY(78),
                new Data().setName("Oxygen").setY(20.9),
                new Data().setName("Other gases").setY(1.1)
        ));
        chartOptions.getSeries().add(seriesPie);

        return highChart;
    }

}