/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.math;

import java.util.Random;

/**
 * Utility and fast math functions.
 * <p/>
 * Thanks to Riven on JavaGaming.org for the basis of sin/cos/atan2/floor/ceil.
 *
 * @author Nathan Sweet
 */
public final class MathUtils {
    public static final float NANO_TO_SEC = 1 / 1000000000f;

    // ---
    public static final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    public static final float PI = 3.1415927f;
    public static final float PI2 = PI * 2;

    public static final float E = 2.7182818f;

    private static final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;

    private static final float RAD_FULL = PI * 2;
    private static final float DEG_FULL = 360;
    private static final float RAD_TO_INDEX = SIN_COUNT / RAD_FULL;
    private static final float DEG_TO_INDEX = SIN_COUNT / DEG_FULL;

    /**
     * multiply by this to convert from radians to degrees
     */
    public static final float radiansToDegrees = 180f / PI;
    public static final float radDeg = radiansToDegrees;
    /**
     * multiply by this to convert from degrees to radians
     */
    public static final float DEGREES_TO_RADIANS = PI / 180;
    public static final float DEG_RAD = DEGREES_TO_RADIANS;

    private static class Sin {
        static final float[] TABLE = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++) {
                TABLE[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * RAD_FULL);
            }
            for (int i = 0; i < 360; i += 90) {
                TABLE[(int) (i * DEG_TO_INDEX) & SIN_MASK] = (float) Math.sin(i * DEGREES_TO_RADIANS);
            }
        }
    }

    /**
     * Returns the sine in radians from a lookup table.
     */
    public static float sin(float radians) {
        return Sin.TABLE[(int) (radians * RAD_TO_INDEX) & SIN_MASK];
    }

    /**
     * Returns the cosine in radians from a lookup table.
     */
    public static float cos(float radians) {
        return Sin.TABLE[(int) ((radians + PI / 2) * RAD_TO_INDEX) & SIN_MASK];
    }

    /**
     * Returns the sine in radians from a lookup table.
     */
    public static float sinDeg(float degrees) {
        return Sin.TABLE[(int) (degrees * DEG_TO_INDEX) & SIN_MASK];
    }

    /**
     * Returns the cosine in radians from a lookup table.
     */
    public static float cosDeg(float degrees) {
        return Sin.TABLE[(int) ((degrees + 90) * DEG_TO_INDEX) & SIN_MASK];
    }

    // ---

    private static final int ATAN2_BITS = 7; // Adjust for accuracy.
    private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
    private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
    private static final int ATAN2_COUNT = ATAN2_MASK + 1;
    static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
    private static final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

    private static class Atan2 {
        static final float[] TABLE = new float[ATAN2_COUNT];

        static {
            for (int i = 0; i < ATAN2_DIM; i++) {
                for (int j = 0; j < ATAN2_DIM; j++) {
                    float x0 = (float) i / ATAN2_DIM;
                    float y0 = (float) j / ATAN2_DIM;
                    TABLE[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
                }
            }
        }
    }

    /**
     * Returns atan2 in radians from a lookup table.
     */
    public static float atan2(float y, float x) {
        float add, mul;
        if (x < 0) {
            if (y < 0) {
                y = -y;
                mul = 1;
            } else {
                mul = -1;
            }
            x = -x;
            add = -PI;
        } else {
            if (y < 0) {
                y = -y;
                mul = -1;
            } else {
                mul = 1;
            }
            add = 0;
        }
        float invDiv = 1 / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);

        if (invDiv == Float.POSITIVE_INFINITY) {
            return ((float) Math.atan2(y, x) + add) * mul;
        }

        int xi = (int) (x * invDiv);
        int yi = (int) (y * invDiv);
        return (Atan2.TABLE[yi * ATAN2_DIM + xi] + add) * mul;
    }

    // ---

    public static Random random = new RandomXS128();

    /**
     * Returns a random number between 0 (inclusive) and the specified value (inclusive).
     */
    public static int random(int range) {
        return random.nextInt(range + 1);
    }

    /**
     * Returns a random number between start (inclusive) and end (inclusive).
     */
    public static int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    /**
     * Returns a random boolean value.
     */
    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    /**
     * Returns true if a random value between 0 and 1 is less than the specified value.
     */
    public static boolean randomBoolean(float chance) {
        return MathUtils.random() < chance;
    }

    /**
     * Returns random number between 0.0 (inclusive) and 1.0 (exclusive).
     */
    public static float random() {
        return random.nextFloat();
    }

    /**
     * Returns a random number between 0 (inclusive) and the specified value (exclusive).
     */
    public static float random(float range) {
        return random.nextFloat() * range;
    }

    /**
     * Returns a random number between start (inclusive) and end (exclusive).
     */
    public static float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    /**
     * Returns -1 or 1, randomly.
     */
    public static int randomSign() {
        return 1 | (random.nextInt() >> 31);
    }

    /**
     * Returns a triangularly distributed random number between -1.0 (exclusive) and 1.0 (exclusive), where values around zero are
     * more likely.
     * <p/>
     * This is an optimized version of {@link #randomTriangular(float, float, float) randomTriangular(-1, 1, 0)}
     */
    public static float randomTriangular() {
        return random.nextFloat() - random.nextFloat();
    }

    /**
     * Returns a triangularly distributed random number between {@code -max} (exclusive) and {@code max} (exclusive), where values
     * around zero are more likely.
     * <p/>
     * This is an optimized version of {@link #randomTriangular(float, float, float) randomTriangular(-max, max, 0)}
     *
     * @param max the upper limit
     */
    public static float randomTriangular(float max) {
        return (random.nextFloat() - random.nextFloat()) * max;
    }

    /**
     * Returns a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive), where the
     * {@code mode} argument defaults to the midpoint between the bounds, giving a symmetric distribution.
     * <p/>
     * This method is equivalent of {@link #randomTriangular(float, float, float) randomTriangular(min, max, (max - min) * .5f)}
     *
     * @param min the lower limit
     * @param max the upper limit
     */
    public static float randomTriangular(float min, float max) {
        return randomTriangular(min, max, (max - min) * .5f);
    }

    /**
     * Returns a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive), where values
     * around {@code mode} are more likely.
     *
     * @param min  the lower limit
     * @param max  the upper limit
     * @param mode the point around which the values are more likely
     */
    public static float randomTriangular(float min, float max, float mode) {
        float u = random.nextFloat();
        float d = max - min;
        if (u <= (mode - min) / d) {
            return min + (float) Math.sqrt(u * d * (mode - min));
        }
        return max - (float) Math.sqrt((1 - u) * d * (max - mode));
    }

    // ---

    /**
     * Returns the next power of two. Returns the specified value if the value is already a power of two.
     */
    public static int nextPowerOfTwo(int value) {
        if (value == 0) {
            return 1;
        }
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    // ---

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static short clamp(short value, short min, short max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    // ---

    /**
     * Linearly interpolates between fromValue to toValue on progress position.
     */
    public static float lerp(float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    // ---

    private static final int BIG_ENOUGH_INT = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double CEIL = 0.9999999;
    private static final double BIG_ENOUGH_CEIL = 16384.999999999996;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    /**
     * Returns the largest integer less than or equal to the specified float. This method will only properly floor floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14).
     */
    public static int floor(float x) {
        return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the largest integer less than or equal to the specified float. This method will only properly floor floats that are
     * positive. Note this method simply casts the float to int.
     */
    public static int floorPositive(float x) {
        return (int) x;
    }

    /**
     * Returns the smallest integer greater than or equal to the specified float. This method will only properly ceil floats from
     * -(2^14) to (Float.MAX_VALUE - 2^14).
     */
    public static int ceil(float x) {
        return (int) (x + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the smallest integer greater than or equal to the specified float. This method will only properly ceil floats that
     * are positive.
     */
    public static int ceilPositive(float x) {
        return (int) (x + CEIL);
    }

    /**
     * Returns the closest integer to the specified float. This method will only properly round floats from -(2^14) to
     * (Float.MAX_VALUE - 2^14).
     */
    public static int round(float x) {
        return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    /**
     * Returns the closest integer to the specified float. This method will only properly round floats that are positive.
     */
    public static int roundPositive(float x) {
        return (int) (x + 0.5f);
    }

    /**
     * Returns true if the value is zero (using the default tolerance as upper bound)
     */
    public static boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * Returns true if the value is zero.
     *
     * @param tolerance represent an upper bound below which the value is considered zero.
     */
    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    /**
     * Returns true if a is nearly equal to b. The function uses the default floating error tolerance.
     *
     * @param a the first value.
     * @param b the second value.
     */
    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * Returns true if a is nearly equal to b.
     *
     * @param a         the first value.
     * @param b         the second value.
     * @param tolerance represent an upper bound below which the two values are considered equal.
     */
    public static boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    /**
     * @return the logarithm of x with base a
     */
    public static float log(float a, float x) {
        return (float) (Math.log(x) / Math.log(a));
    }

    /**
     * @return the logarithm of x with base 2
     */
    public static float log2(float x) {
        return log(2, x);
    }
}
