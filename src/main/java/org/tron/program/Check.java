package org.tron.program;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.storage.leveldb.LevelDbDataSourceImpl;
import org.tron.core.Constant;
import org.tron.core.Wallet;
import org.tron.core.capsule.TransactionInfoCapsule;
import org.tron.core.config.args.Args;
import org.tron.core.exception.BadItemException;
import org.tron.protos.Protocol.InternalTransaction;

@Slf4j
public class Check {

  public static void main(String[] args) throws InterruptedException {
    Args.setParam(args, Constant.TESTNET_CONF);
    Args cfgArgs = Args.getInstance();
    Wallet.setAddressPreFixString(Constant.ADD_PRE_FIX_STRING_MAINNET);
    Wallet.setAddressPreFixByte(Constant.ADD_PRE_FIX_BYTE_MAINNET);
    LevelDbDataSourceImpl dataSource = new LevelDbDataSourceImpl(
        Args.getInstance().getOutputDirectory(), "transactionHistoryStore");

    dataSource.initDB();

    AtomicLong counter = new AtomicLong(0);
    dataSource.stream().forEach( dataItem -> {
      TransactionInfoCapsule infoCapsule = null;
      try {
        infoCapsule = new TransactionInfoCapsule(dataItem.getValue());
      } catch (BadItemException e) {
        e.printStackTrace();
      }
      List<InternalTransaction> internalList = infoCapsule.getInstance().getInternalTransactionsList();
      if(counter.getAndIncrement() % 10000 ==0) {
        System.out.println("Counter:" + counter.get());
      }
      if(internalList.isEmpty()) return;
      TransactionInfoCapsule finalInfoCapsule = infoCapsule;
      internalList.forEach(internalItem -> {
//s
        internalItem.getCallValueInfoOrBuilderList().forEach(element->{
            logger.info("tokenId:" + element.getTokenId());
            if (element.getTokenId().equals("0")){
              logger.error("0");
            }
        });
      });
    });
    dataSource.closeDB();

  }
}
