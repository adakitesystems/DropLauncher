/*
 * Copyright (C) 2017 Adakite
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package droplauncher.bwta;

import droplauncher.DropLauncher;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BWTA {

  public enum CacheV1 {

    BWTA_V1_0409CA0("0409ca0d7fe0c7f4083a70996a8f28f664d2fe37.bwta"),
    BWTA_V1_041C819("041c81938eede5293a9855e90410c775469ebb56.bwta"),
    BWTA_V1_130C43F("130c43f080bf497b946b6b9118eb4cba594a35ac.bwta"),
    BWTA_V1_1542E57("1542e57f708d411fbce62dd1b4769003e69d8b35.bwta"),
    BWTA_V1_178F4E5("178f4e569f840d29349356bd0a985fc5f28746a6.bwta"),
    BWTA_V1_1DFB024("1dfb024d7ac9ddd1629e06a3902d290c34c777f7.bwta"),
    BWTA_V1_1E983EB("1e983eb6bcfa02ef7d75bd572cb59ad3aab49285.bwta"),
    BWTA_V1_206B340("206b3405b7cce0a0df13f49ec050f82025966e37.bwta"),
    BWTA_V1_24F8796("24f8796f23a97221f1021e3d4d2393b68a311b2e.bwta"),
    BWTA_V1_2B071DD("2b071dd509c8186dfd2c07476536be644d9d9d18.bwta"),
    BWTA_V1_2C18612("2c186126a8e3eb2fa4497c305e119bdb4f0516f4.bwta"),
    BWTA_V1_3078EE9("3078ee93e4a0c3c2ad22c73ab62ef806d9436c3d.bwta"),
    BWTA_V1_3962B76("3962b76f1f67db527b4f90bc6a4b0165ef3ac9a6.bwta"),
    BWTA_V1_39BF400("39bf400dbb3ee3b5d6710e8ca410c727c4636560.bwta"),
    BWTA_V1_3BA63BA("3ba63babc573cda0c5ce77074d0d877bf630c7c8.bwta"),
    BWTA_V1_3FDED17("3fded17c58a8c42e3b44b9a29c8e2141741d2606.bwta"),
    BWTA_V1_40AAA54("40aaa54886da87b83632b42547ce53f17d757135.bwta"),
    BWTA_V1_4202010("4202010e86ce1eb4eca38c97bacdbb5cea4addbd.bwta"),
    BWTA_V1_4493525("4493525656b56eb7272c1997016345dc221fea7e.bwta"),
    BWTA_V1_450A792("450a792de0e544b51af5de578061cb8a2f020f32.bwta"),
    BWTA_V1_4B5E1F7("4b5e1f7a14bb22d0265aacb6e7178888c92dda9d.bwta"),
    BWTA_V1_4E24F21("4e24f217d2fe4dbfa6799bc57f74d8dc939d425b.bwta"),
    BWTA_V1_50674B2("50674b24bcc2ebeb58c6046ceace1fdc4f237c70.bwta"),
    BWTA_V1_5183D66("5183d669f47d2c8b67c0bde645e0942f5d8c71d8.bwta"),
    BWTA_V1_5731C10("5731c103687826de48ba3cc7d6e37e2537b0e902.bwta"),
    BWTA_V1_5DAB9AF("5dab9af39a609587953354d499f7745d02656720.bwta"),
    BWTA_V1_63E0DD1("63e0dd1ed52f52153c3ba2a39d4eebe8128f0f86.bwta"),
    BWTA_V1_6756C2F("6756c2f3364f06ab44333202f5199b62cb038c7a.bwta"),
    BWTA_V1_6CB7371("6cb737167d634e089bc05390d8e2e209ef8e5dbf.bwta"),
    BWTA_V1_6E07B55("6e07b55227758ecd763282578e2d1ddf6295f0d4.bwta"),
    BWTA_V1_6E8DB72("6e8db723743bcc0fb26ba5020452c52e9869b87f.bwta"),
    BWTA_V1_6F8DA3C("6f8da3c3cc8d08d9cf882700efa049280aedca8c.bwta"),
    BWTA_V1_70D7597("70d7597ff7d04c0952782ba67a2d3e06eea29af5.bwta"),
    BWTA_V1_71D1177("71d1177f7cc5522915b8d0ca000ecf1a4ac2e5e1.bwta"),
    BWTA_V1_78354BF("78354bf12b63614e78abf0334a5f1d74a2e51139.bwta"),
    BWTA_V1_7A54577("7a545777b2b51015778e35794b7c3634f8eae143.bwta"),
    BWTA_V1_7A6D1EE("7a6d1ee3663e1d320aaaa63d11dd805de4a6d299.bwta"),
    BWTA_V1_7D10FDC("7d10fdc92559534b537c69afb8417d0fc8626e74.bwta"),
    BWTA_V1_83320E5("83320e505f35c65324e93510ce2eafbaa71c9aa1.bwta"),
    BWTA_V1_8B36CBC("8b36cbc2df21bc628dd04e623408950638cdcbde.bwta"),
    BWTA_V1_9505D61("9505d618c63a0959f0c0bfe21c253a2ea6e58d26.bwta"),
    BWTA_V1_9A4498A("9a4498a896b28d115129624f1c05322f48188fe0.bwta"),
    BWTA_V1_9BFC271("9bfc271360fa5bab3707a29e1326b84d0ff58911.bwta"),
    BWTA_V1_A220D93("a220d93efdf05a439b83546a579953c63c863ca7.bwta"),
    BWTA_V1_A3968F6("a3968f6994632403f8c581c04c46672093d6ff0d.bwta"),
    BWTA_V1_A8A7D20("a8a7d202f572bb61b256c5315533a7d9a1527d02.bwta"),
    BWTA_V1_AF618EA("af618ea3ed8a8926ca7b17619eebcb9126f0d8b1.bwta"),
    BWTA_V1_B10E73A("b10e73a252d5c693f19829871a01043f0277fd58.bwta"),
    BWTA_V1_B8C7459("b8c7459bb3f86cdc3f9e95b52ba51b434a5cdf01.bwta"),
    BWTA_V1_B997DBC("b997dbc7792e7067668ff56a33793a43b35ffdd2.bwta"),
    BWTA_V1_BA2FC0E("ba2fc0ed637e4ec91cc70424335b3c13e131b75a.bwta"),
    BWTA_V1_BC0CA37("bc0ca37582855b18ce6f479e017d1961ef9182dc.bwta"),
    BWTA_V1_C8386B8("c8386b87051f6773f6b2681b0e8318244aa086a6.bwta"),
    BWTA_V1_C9F06CC("c9f06ccc8fb15e28a6f97bfd08b13b4ab25dc446.bwta"),
    BWTA_V1_CB39A18("cb39a180c36de73e887e8430c5f32197c52c18ba.bwta"),
    BWTA_V1_CD5D907("cd5d907c30d58333ce47c88719b6ddb2cba6612f.bwta"),
    BWTA_V1_D2F5633("d2f5633cc4bb0fca13cd1250729d5530c82c7451.bwta"),
    BWTA_V1_DBC596B("dbc596b6d00052f57e755b86bd3a8ff7db7a030a.bwta"),
    BWTA_V1_DD6CC01("dd6cc01423e8c0fbcc2d9b0bf79f541ec646c3e3.bwta"),
    BWTA_V1_DE2ADA7("de2ada75fbc741cfa261ee467bf6416b10f9e301.bwta"),
    BWTA_V1_DF21AC8("df21ac8f19f805e1e0d4e9aa9484969528195d9f.bwta"),
    BWTA_V1_E3FE3AB("e3fe3ab70fd8fb7f5ac5e8263f70ef99917c5494.bwta"),
    BWTA_V1_E47775E("e47775e171fe3f67cc2946825f00a6993b5a415e.bwta"),
    BWTA_V1_E6D0144("e6d0144e14315118d916905ff5e7045f68db541e.bwta"),
    BWTA_V1_E74CFAE("e74cfae60e98fefe49b6138461c2a02438b4a821.bwta"),
    BWTA_V1_ECFF6C5("ecff6c5ec10668ac81aef9dfe4021c8fa397dc14.bwta"),
    BWTA_V1_F03FD2A("f03fd2a7144d21f6a1d5bc1bd3554746ca7db4d6.bwta"),
    BWTA_V1_F05031C("f05031cd2c28b8f688e3ccdcb2fd2699d3496905.bwta"),
    BWTA_V1_FB98F70("fb98f70052bd29c0cfea343cf9608a5790a21d8b.bwta"),
    BWTA_V1_FE503A7("fe503a78c7375b3f549dc1a5e7a9ce360f195973.bwta")
    ;

    private final String str;

    private CacheV1(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  public enum CacheV2 {

    BWTA_V2_0409CA0("0409ca0d7fe0c7f4083a70996a8f28f664d2fe37.bwta"),
    BWTA_V2_0409CA0_K("0409ca0d7fe0c7f4083a70996a8f28f664d2fe37krasi0.bwta"),
    BWTA_V2_041C819("041c81938eede5293a9855e90410c775469ebb56.bwta"),
    BWTA_V2_130C43F("130c43f080bf497b946b6b9118eb4cba594a35ac.bwta"),
    BWTA_V2_1542E57("1542e57f708d411fbce62dd1b4769003e69d8b35.bwta"),
    BWTA_V2_178F4E5("178f4e569f840d29349356bd0a985fc5f28746a6.bwta"),
    BWTA_V2_1DFB024("1dfb024d7ac9ddd1629e06a3902d290c34c777f7.bwta"),
    BWTA_V2_1E983EB("1e983eb6bcfa02ef7d75bd572cb59ad3aab49285.bwta"),
    BWTA_V2_1E983EB_K("1e983eb6bcfa02ef7d75bd572cb59ad3aab49285krasi0.bwta"),
    BWTA_V2_206B340("206b3405b7cce0a0df13f49ec050f82025966e37.bwta"),
    BWTA_V2_24F8796("24f8796f23a97221f1021e3d4d2393b68a311b2e.bwta"),
    BWTA_V2_2B071DD("2b071dd509c8186dfd2c07476536be644d9d9d18.bwta"),
    BWTA_V2_2C18612("2c186126a8e3eb2fa4497c305e119bdb4f0516f4.bwta"),
    BWTA_V2_3078EE9("3078ee93e4a0c3c2ad22c73ab62ef806d9436c3d.bwta"),
    BWTA_V2_3962B76("3962b76f1f67db527b4f90bc6a4b0165ef3ac9a6.bwta"),
    BWTA_V2_39BF400("39bf400dbb3ee3b5d6710e8ca410c727c4636560.bwta"),
    BWTA_V2_3BA63BA("3ba63babc573cda0c5ce77074d0d877bf630c7c8.bwta"),
    BWTA_V2_3FDED17("3fded17c58a8c42e3b44b9a29c8e2141741d2606.bwta"),
    BWTA_V2_40AAA54("40aaa54886da87b83632b42547ce53f17d757135.bwta"),
    BWTA_V2_4202010("4202010e86ce1eb4eca38c97bacdbb5cea4addbd.bwta"),
    BWTA_V2_4493525("4493525656b56eb7272c1997016345dc221fea7e.bwta"),
    BWTA_V2_450A792("450a792de0e544b51af5de578061cb8a2f020f32.bwta"),
    BWTA_V2_450A792_K("450a792de0e544b51af5de578061cb8a2f020f32krasi0.bwta"),
    BWTA_V2_4B5E1F7("4b5e1f7a14bb22d0265aacb6e7178888c92dda9d.bwta"),
    BWTA_V2_4E24F21("4e24f217d2fe4dbfa6799bc57f74d8dc939d425b.bwta"),
    BWTA_V2_4E24F21_K("4e24f217d2fe4dbfa6799bc57f74d8dc939d425bkrasi0.bwta"),
    BWTA_V2_50674B2("50674b24bcc2ebeb58c6046ceace1fdc4f237c70.bwta"),
    BWTA_V2_5183D66("5183d669f47d2c8b67c0bde645e0942f5d8c71d8.bwta"),
    BWTA_V2_5731C10("5731c103687826de48ba3cc7d6e37e2537b0e902.bwta"),
    BWTA_V2_5DAB9AF("5dab9af39a609587953354d499f7745d02656720.bwta"),
    BWTA_V2_63E0DD1("63e0dd1ed52f52153c3ba2a39d4eebe8128f0f86.bwta"),
    BWTA_V2_6756C2F("6756c2f3364f06ab44333202f5199b62cb038c7a.bwta"),
    BWTA_V2_6CB7371("6cb737167d634e089bc05390d8e2e209ef8e5dbf.bwta"),
    BWTA_V2_6E07B55("6e07b55227758ecd763282578e2d1ddf6295f0d4.bwta"),
    BWTA_V2_6E8DB72("6e8db723743bcc0fb26ba5020452c52e9869b87f.bwta"),
    BWTA_V2_6F8DA3C("6f8da3c3cc8d08d9cf882700efa049280aedca8c.bwta"),
    BWTA_V2_6F8DA3C_K("6f8da3c3cc8d08d9cf882700efa049280aedca8ckrasi0.bwta"),
    BWTA_V2_70D7597("70d7597ff7d04c0952782ba67a2d3e06eea29af5.bwta"),
    BWTA_V2_71D1177("71d1177f7cc5522915b8d0ca000ecf1a4ac2e5e1.bwta"),
    BWTA_V2_78354BF("78354bf12b63614e78abf0334a5f1d74a2e51139.bwta"),
    BWTA_V2_7A54577("7a545777b2b51015778e35794b7c3634f8eae143.bwta"),
    BWTA_V2_7A6D1EE("7a6d1ee3663e1d320aaaa63d11dd805de4a6d299.bwta"),
    BWTA_V2_7D10FDC("7d10fdc92559534b537c69afb8417d0fc8626e74.bwta"),
    BWTA_V2_83320E5("83320e505f35c65324e93510ce2eafbaa71c9aa1.bwta"),
    BWTA_V2_8B36CBC("8b36cbc2df21bc628dd04e623408950638cdcbde.bwta"),
    BWTA_V2_9505D61("9505d618c63a0959f0c0bfe21c253a2ea6e58d26.bwta"),
    BWTA_V2_9505D61_K("9505d618c63a0959f0c0bfe21c253a2ea6e58d26krasi0.bwta"),
    BWTA_V2_9A4498A("9a4498a896b28d115129624f1c05322f48188fe0.bwta"),
    BWTA_V2_9A4498A_K("9a4498a896b28d115129624f1c05322f48188fe0krasi0.bwta"),
    BWTA_V2_9BFC271("9bfc271360fa5bab3707a29e1326b84d0ff58911.bwta"),
    BWTA_V2_9BFC271_K("9bfc271360fa5bab3707a29e1326b84d0ff58911krasi0.bwta"),
    BWTA_V2_A220D93("a220d93efdf05a439b83546a579953c63c863ca7.bwta"),
    BWTA_V2_A220D93_K("a220d93efdf05a439b83546a579953c63c863ca7krasi0.bwta"),
    BWTA_V2_A3968F6("a3968f6994632403f8c581c04c46672093d6ff0d.bwta"),
    BWTA_V2_A8A7D20("a8a7d202f572bb61b256c5315533a7d9a1527d02.bwta"),
    BWTA_V2_AF618EA("af618ea3ed8a8926ca7b17619eebcb9126f0d8b1.bwta"),
    BWTA_V2_AF618EA_K("af618ea3ed8a8926ca7b17619eebcb9126f0d8b1krasi0.bwta"),
    BWTA_V2_B10E73A("b10e73a252d5c693f19829871a01043f0277fd58.bwta"),
    BWTA_V2_B8C7459("b8c7459bb3f86cdc3f9e95b52ba51b434a5cdf01.bwta"),
    BWTA_V2_B997DBC("b997dbc7792e7067668ff56a33793a43b35ffdd2.bwta"),
    BWTA_V2_BA2FC0E("ba2fc0ed637e4ec91cc70424335b3c13e131b75a.bwta"),
    BWTA_V2_BC0CA37("bc0ca37582855b18ce6f479e017d1961ef9182dc.bwta"),
    BWTA_V2_C8386B8("c8386b87051f6773f6b2681b0e8318244aa086a6.bwta"),
    BWTA_V2_C8386B8_K("c8386b87051f6773f6b2681b0e8318244aa086a6krasi0.bwta"),
    BWTA_V2_C9F06CC("c9f06ccc8fb15e28a6f97bfd08b13b4ab25dc446.bwta"),
    BWTA_V2_CB39A18("cb39a180c36de73e887e8430c5f32197c52c18ba.bwta"),
    BWTA_V2_CD5D907("cd5d907c30d58333ce47c88719b6ddb2cba6612f.bwta"),
    BWTA_V2_D2F5633("d2f5633cc4bb0fca13cd1250729d5530c82c7451.bwta"),
    BWTA_V2_D2F5633_K("d2f5633cc4bb0fca13cd1250729d5530c82c7451krasi0.bwta"),
    BWTA_V2_DBC596B("dbc596b6d00052f57e755b86bd3a8ff7db7a030a.bwta"),
    BWTA_V2_DD6CC01("dd6cc01423e8c0fbcc2d9b0bf79f541ec646c3e3.bwta"),
    BWTA_V2_DE2ADA7("de2ada75fbc741cfa261ee467bf6416b10f9e301.bwta"),
    BWTA_V2_DE2ADA7_K("de2ada75fbc741cfa261ee467bf6416b10f9e301krasi0.bwta"),
    BWTA_V2_DF21AC8("df21ac8f19f805e1e0d4e9aa9484969528195d9f.bwta"),
    BWTA_V2_DF21AC8_K("df21ac8f19f805e1e0d4e9aa9484969528195d9fkrasi0.bwta"),
    BWTA_V2_E3FE3AB("e3fe3ab70fd8fb7f5ac5e8263f70ef99917c5494.bwta"),
    BWTA_V2_E47775E("e47775e171fe3f67cc2946825f00a6993b5a415e.bwta"),
    BWTA_V2_E47775E_K("e47775e171fe3f67cc2946825f00a6993b5a415ekrasi0.bwta"),
    BWTA_V2_E6D0144("e6d0144e14315118d916905ff5e7045f68db541e.bwta"),
    BWTA_V2_E74CFAE("e74cfae60e98fefe49b6138461c2a02438b4a821.bwta"),
    BWTA_V2_ECFF6C5("ecff6c5ec10668ac81aef9dfe4021c8fa397dc14.bwta"),
    BWTA_V2_F03FD2A("f03fd2a7144d21f6a1d5bc1bd3554746ca7db4d6.bwta"),
    BWTA_V2_F05031C("f05031cd2c28b8f688e3ccdcb2fd2699d3496905.bwta"),
    BWTA_V2_FB98F70("fb98f70052bd29c0cfea343cf9608a5790a21d8b.bwta"),
    BWTA_V2_FE503A7("fe503a78c7375b3f549dc1a5e7a9ce360f195973.bwta")
    ;

    private final String str;

    private CacheV2(String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  public static final Path V1_DIRECTORY = Paths.get("BWTA");
  public static final Path V2_DIRECTORY = Paths.get("BWTA2");

//  public static final Path V1_RESOURCE_DIRECTORY = DropLauncher.DATA_DIRECTORY.resolve("bwta-cache").resolve("BWTA");
//  public static final Path V2_RESOURCE_DIRECTORY = DropLauncher.DATA_DIRECTORY.resolve("bwta-cache").resolve("BWTA2");

  public static final Path CACHE_ARCHIVE_FILE = DropLauncher.DATA_DIRECTORY.resolve("bwta-cache.zip");

  private BWTA() {}

}
