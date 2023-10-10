package org.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents params that can be used in ecl file instead of hardcoded values.
 * 
 * @author Mauro Sonzogni
 * 
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EclParams {
    /**
     * Threshold value used for distance.
     */
    private Double threshold = 0.5;

    /**
     * The value represent the weigth assigned to the component distance on the total distance
     */
    private Double componentDistanceWeigth = 0.5;

    /**
     * The value represent the weigth assigned to the connector distance on the total distance
     */
    private Double connectorDistanceWeigth = 0.5;

}
