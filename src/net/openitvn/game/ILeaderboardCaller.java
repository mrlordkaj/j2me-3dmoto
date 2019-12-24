/*
 * Copyright (C) 2012 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.openitvn.game;

/**
 *
 * @author Thinh Pham
 */
public interface ILeaderboardCaller {
    public void onSubmitSuccess();
    public void onSubmitFail();

    public void onGetRankSuccess(int rank);
    public void onGetRankFail();

    public void onView7Success(StringBuffer reader);
    public void onView7Fail();

    public void onViewAllSuccess(StringBuffer reader);
    public void onViewAllFail();

    public void onRemoveSuccess();
    public void onRemoveFail();
}
