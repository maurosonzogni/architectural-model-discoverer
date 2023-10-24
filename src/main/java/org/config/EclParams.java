package org.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents params that can be used in ecl file instead of hardcoded values.
 * 
 * @author Mauro Sonzogni
 * 
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EclParams {
    /**
     * Threshold value used for distance.
     */
    private Double threshold = 0.5;

    /* NOTE: The sum of weigth variables must be equal to 1 */

    /**
     * The value represent the weigth assigned to the component distance on the total distance
     */
    private Double componentWeigth = 0.4;

    /**
     * The value represent the weigth assigned to the connection distance on the total distance
     */
    private Double connectionWeigth = 0.4;

    /**
     * The value represent the weigth assigned to the feature distance on the total distance
     */
    private Double featureWeigth = 0.2;

}
