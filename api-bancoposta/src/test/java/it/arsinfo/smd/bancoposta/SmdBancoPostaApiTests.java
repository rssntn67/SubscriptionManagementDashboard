package it.arsinfo.smd.bancoposta;

import it.arsinfo.smd.bancoposta.impl.BancoPostaServiceImpl;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.DistintaVersamento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class SmdBancoPostaApiTests {

    @Test
    public void testIncassoTelematiciApi() {
        String riepilogo1="4000063470009171006              999000000010000000015000000000100000000150000000000000000000000                                                                                                        \n";
        Set<String> versamenti1= new HashSet<>();
        versamenti1.add("0000000000000010000634700091710046740000001500055111092171006000000018000792609CCN                                                                                                                      \n");
        DistintaVersamento dv =  BancoPostaServiceImpl.generaIncasso(versamenti1, riepilogo1);
        Assertions.assertNotNull(dv);
        Assertions.assertEquals(Cassa.Ccp,dv.getCassa());
        Assertions.assertEquals(Ccp.UNO,dv.getCcp());
        Assertions.assertEquals(Cuas.TELEMATICI,dv.getCuas());
        Assertions.assertEquals(1,dv.getItems().size());
    }


    @Test
    public void testIncassoVenezia() {
        String riepilogo2="3000063470009171006              999000000090000000367000000000700000003020000000002000000006500                                                                                                        \n";
        Set<String> versamenti2= new HashSet<>();
        versamenti2.add("0865737400000020000634700091710056740000001500074046022171006000000018000854368DIN                                                                                                                      \n");
        versamenti2.add("0865298400000030000634700091710056740000001800076241052171006000000018000263519DIN                                                                                                                      \n");
        versamenti2.add("0863439100000040000634700091710056740000003000023013042171006000000018000254017DIN                                                                                                                      \n");
        versamenti2.add("0854922500000050000634700091710046740000003700023367052171006000000018000761469DIN                                                                                                                      \n");
        versamenti2.add("0863439000000060000634700091710056740000004800023013042171006000000018000253916DIN                                                                                                                      \n");
        versamenti2.add("0865570900000070000634700091710056740000007000023247042171006000000018000800386DIN                                                                                                                      \n");
        versamenti2.add("0863569900000080000634700091710056740000008400074264032171006000000018000508854DIN                                                                                                                      \n");
        versamenti2.add("0856588699999990000634700091710041230000001500038124062171006727703812406007375DIN                                                                                                                      \n");
        versamenti2.add("0858313299999990000634700091710041230000005000098101062171006727709810106010156DIN                                                                                                                      \n");

        DistintaVersamento dv =BancoPostaServiceImpl.generaIncasso(versamenti2, riepilogo2);

        Assertions.assertNotNull(dv);
        Assertions.assertEquals(Cassa.Ccp,dv.getCassa());
        Assertions.assertEquals(Ccp.UNO,dv.getCcp());
        Assertions.assertEquals(Cuas.VENEZIA,dv.getCuas());
        Assertions.assertEquals(9,dv.getItems().size());

    }

    @Test
    public void testIncassoFirenze() {
        String riepilogo3="5000063470009171006              999000000060000000201000000000500000001810000000001000000002000                                                                                                        \n";
        Set<String> versamenti3= new HashSet<>();
        versamenti3.add("0854174400000090000634700091710046740000001000055379072171006000000018000686968DIN                                                                                                                      \n");
        versamenti3.add("0860359800000100000634700091710056740000001500055239072171006000000018000198318DIN                                                                                                                      \n");
        versamenti3.add("0858363300000110000634700091710056740000001500055826052171006000000018000201449DIN                                                                                                                      \n");
        versamenti3.add("0860441300000120000634700091710056740000003300055820042171006000000018000633491DIN                                                                                                                      \n");
        versamenti3.add("0860565700000130000634700091710056740000010800055917062171006000000018000196500DIN                                                                                                                      \n");
        versamenti3.add("0855941199999990000634700091710041230000002000055681052171006727705568105003308DIN                                                                                                                      \n");

        DistintaVersamento dv =BancoPostaServiceImpl.generaIncasso(versamenti3, riepilogo3);

        Assertions.assertNotNull(dv);
        Assertions.assertEquals(Cassa.Ccp,dv.getCassa());
        Assertions.assertEquals(Ccp.UNO,dv.getCcp());
        Assertions.assertEquals(Cuas.FIRENZE,dv.getCuas());
        Assertions.assertEquals(6,dv.getItems().size());
    }

    @Test
    public void testIncassoBari() {

        String riepilogo4="7000063470009171006              999000000070000000447500000000400000001750000000003000000027250                                                                                                        \n";
        Set<String> versamenti4= new HashSet<>();
        versamenti4.add("0873460200000140000634700091710056740000001200053057032171006000000018000106227DIN                                                                                                                      \n");
        versamenti4.add("0874263500000150000634700091710056740000003600009019032171006000000018000077317DIN                                                                                                                      \n");
        versamenti4.add("0875677100000160000634700091710056740000006000029079022171006000000018000125029DIN                                                                                                                      \n");
        versamenti4.add("0871026300000170000634700091710046740000006700040366032171006000000018000065383DIN                                                                                                                      \n");
        versamenti4.add("0862740599999990000634700091710044510000000750002066172171006727700206617006437DIN                                                                                                                      \n");
        versamenti4.add("0857504199999990000634700091710034510000004000040016062171006727604001606035576DIN                                                                                                                      \n");
        versamenti4.add("0866089199999990000634700091710044510000022500018160052171006727701816005010892DIN                                                                                                                      \n");

        DistintaVersamento dv =BancoPostaServiceImpl.generaIncasso(versamenti4, riepilogo4);

        Assertions.assertNotNull(dv);
        Assertions.assertEquals(Cassa.Ccp,dv.getCassa());
        Assertions.assertEquals(Ccp.UNO,dv.getCcp());
        Assertions.assertEquals(Cuas.BARI,dv.getCuas());
        Assertions.assertEquals(7,dv.getItems().size());
    }


}
