package effectiveMobile.bank.util.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Check;

@AllArgsConstructor
@Getter
public class UnitTransferDto {
    private int toId;
    private double amount;
}