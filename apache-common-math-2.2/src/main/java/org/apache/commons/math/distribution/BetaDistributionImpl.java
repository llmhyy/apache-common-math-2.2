/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.FastMath;

/**
 * Implements the Beta distribution.
 * <p>
 * References:
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Beta_distribution">
 * Beta distribution</a></li>
 * </ul>
 * </p>
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 * @since 2.0
 */
public class BetaDistributionImpl
    extends AbstractContinuousDistribution implements BetaDistribution {

	/**
     * Default inverse cumulative probability accuracy
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;

    /** Serializable version identifier. */
    private static final long serialVersionUID = -1221965979403477668L;

    /** First shape parameter. */
    private double alpha;

    /** Second shape parameter. */
    private double beta;

    /** Normalizing factor used in density computations.
     * updated whenever alpha or beta are changed.
     */
    private double z;

    /** Inverse cumulative probability accuracy */
    private final double solverAbsoluteAccuracy;

    /**
     * Build a new instance.
     * @param alpha first shape parameter (must be positive)
     * @param beta second shape parameter (must be positive)
     * @param inverseCumAccuracy the maximum absolute error in inverse cumulative probability estimates
     * (defaults to {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY})
     * @since 2.1
     */
    public BetaDistributionImpl(double alpha, double beta, double inverseCumAccuracy) {
        this.alpha = alpha;
        this.beta = beta;
        z = Double.NaN;
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * Build a new instance.
     * @param alpha first shape parameter (must be positive)
     * @param beta second shape parameter (must be positive)
     */
    public BetaDistributionImpl(double alpha, double beta) {
        this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /** {@inheritDoc}
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setAlpha(double alpha) {
        this.alpha = alpha;
        z = Double.NaN;
    }

    /** {@inheritDoc} */
    public double getAlpha() {
        return alpha;
    }

    /** {@inheritDoc}
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setBeta(double beta) {
        this.beta = beta;
        z = Double.NaN;
    }

    /** {@inheritDoc} */
    public double getBeta() {
        return beta;
    }

    /**
     * Recompute the normalization factor.
     */
    public void recomputeZ() {
        if (Double.isNaN(z)) {
            z = Gamma.logGamma(alpha) + Gamma.logGamma(beta) - Gamma.logGamma(alpha + beta);
        }
    }

    /**
     * Return the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     * @deprecated
     */
    @Deprecated
    public double density(Double x) {
        return density(x.doubleValue());
    }

    /**
     * Return the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     * @since 2.1
     */
    @Override
    public double density(double x) {
        recomputeZ();
        if (x < 0 || x > 1) {
            return 0;
        } else if (x == 0) {
            if (alpha < 1) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA, alpha);
            }
            return 0;
        } else if (x == 1) {
            if (beta < 1) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA, beta);
            }
            return 0;
        } else {
            double logX = FastMath.log(x);
            double log1mX = FastMath.log1p(-x);
            return FastMath.exp((alpha - 1) * logX + (beta - 1) * log1mX - z);
        }
    }

    /** {@inheritDoc} */
    @Override
    public double inverseCumulativeProbability(double p) throws MathException {
        if (p == 0) {
            return 0;
        } else if (p == 1) {
            return 1;
        } else {
            return super.inverseCumulativeProbability(p);
        }
    }

    /** {@inheritDoc} */
    @Override
    public double getInitialDomain(double p) {
        return p;
    }

    /** {@inheritDoc} */
    @Override
    public double getDomainLowerBound(double p) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public double getDomainUpperBound(double p) {
        return 1;
    }

    /** {@inheritDoc} */
    public double cumulativeProbability(double x) throws MathException {
        if (x <= 0) {
            return 0;
        } else if (x >= 1) {
            return 1;
        } else {
            return Beta.regularizedBeta(x, alpha, beta);
        }
    }

    /** {@inheritDoc} */
    @Override
    public double cumulativeProbability(double x0, double x1) throws MathException {
        return cumulativeProbability(x1) - cumulativeProbability(x0);
    }

    /**
     * Return the absolute accuracy setting of the solver used to estimate
     * inverse cumulative probabilities.
     *
     * @return the solver absolute accuracy
     * @since 2.1
     */
    @Override
    public double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }

    /**
     * Returns the lower bound of the support for this distribution.
     * The support of the Beta distribution is always [0, 1], regardless
     * of the parameters, so this method always returns 0.
     *
     * @return lower bound of the support (always 0)
     * @since 2.2
     */
    public double getSupportLowerBound() {
        return 0;
    }

    /**
     * Returns the upper bound of the support for this distribution.
     * The support of the Beta distribution is always [0, 1], regardless
     * of the parameters, so this method always returns 1.
     *
     * @return lower bound of the support (always 1)
     * @since 2.2
     */
    public double getSupportUpperBound() {
        return 1;
    }

    /**
     * Returns the mean.
     *
     * For first shape parameter <code>s1</code> and
     * second shape parameter <code>s2</code>, the mean is
     * <code>s1 / (s1 + s2)</code>
     *
     * @return the mean
     * @since 2.2
     */
    public double getNumericalMean() {
        final double a = getAlpha();
        return a / (a + getBeta());
    }

    /**
     * Returns the variance.
     *
     * For first shape parameter <code>s1</code> and
     * second shape parameter <code>s2</code>,
     * the variance is
     * <code>[ s1 * s2 ] / [ (s1 + s2)^2 * (s1 + s2 + 1) ]</code>
     *
     * @return the variance
     * @since 2.2
     */
    public double getNumericalVariance() {
        final double a = getAlpha();
        final double b = getBeta();
        final double alphabetasum = a + b;
        return (a * b) / ((alphabetasum * alphabetasum) * (alphabetasum + 1));
    }

    public static void main(String[] args) throws MathException {

        double[] x = new double[]{-0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1};
        double alpha = 0.1, beta = 0.1;
        double[] cumes = new double[]{
                0.0000000000, 0.0000000000, 0.4063850939, 0.4397091902, 0.4628041861,
                0.4821200456, 0.5000000000, 0.5178799544, 0.5371958139, 0.5602908098,
                0.5936149061, 1.0000000000, 1.0000000000};
        BetaDistribution d = new BetaDistributionImpl(alpha, beta);
        for (int i = 0; i < x.length; i++) {
            System.out.println(cumes[i]==d.cumulativeProbability(x[i]));
            System.out.println(cumes[i]==1e-8);
        }

	}

	public void setZ(double z) {
		this.z = z;
	}

    public static double getDefaultInverseAbsoluteAccuracy() {
		return DEFAULT_INVERSE_ABSOLUTE_ACCURACY;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public double getZ() {
		return z;
	}
}
