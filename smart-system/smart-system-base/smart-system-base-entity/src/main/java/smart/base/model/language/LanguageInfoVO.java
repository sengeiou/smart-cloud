package smart.base.model.language;

import lombok.Data;

import java.util.List;

@Data
public class LanguageInfoVO extends LanguageInfoModel{
    List<LanguageCrModel> translateList;
}
