package br.assistentediscente.api.main.formatter.schedule;

import br.assistentediscente.api.main.formatter.BaseFormatter;
import org.springframework.context.MessageSource;

public abstract class BaseScheduleFormatter extends BaseFormatter {

     public BaseScheduleFormatter(MessageSource messageSource) {
          super(messageSource);
     }

     protected static String getHoraryResult(String[] schedule) {
          StringBuilder horaryResult = new StringBuilder();

          for(int i = 0 ; i < schedule.length; i++) {
               String[] horary = schedule[i].split(";");

               if(i>0) {
                    String[] previousHours = schedule[i - 1].split(";");

                    if (previousHours[1].equals(horary[0])) {
                         if (horary[1].substring(0, 5).contains("00"))
                              horaryResult.append(horary[1], 0, 2);
                         else
                              horaryResult.append(horary[1].replace(":", " e "), 0, 7);

                         horaryResult.append(", de ");
                    } else {
                         if (horary[0].substring(0, 5).contains("00"))
                              horaryResult.append(horary[0], 0, 2);
                         else
                              horaryResult.append(horary[0].replace(":", " e "), 0, 7);

                         horaryResult.append(" até ");
                    }
               }else {
                    if (horary[0].substring(0, 5).contains("00"))
                         horaryResult.append(horary[0], 0, 2);
                    else
                         horaryResult.append(horary[0].replace(":", " e "), 0, 7);
                    horaryResult.append(" até ");
               }
          }
          horaryResult.setLength(horaryResult.length()-5);
          return horaryResult.toString();
     }

}
