<template>
  <el-dialog title="导出数据" :close-on-click-modal="false" :visible.sync="visible"
    class="JNPF-dialog JNPF-dialog_center" lock-scroll width="600px">
    <el-form label-position="top" label-width="80px">
      <el-form-item label="数据选择">
        <el-radio-group v-model="type">
          <el-radio :label="0">当前页面数据</el-radio>
          <el-radio :label="1">全部页面数据</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="导出字段">
        <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll"
          @change="handleCheckAllChange">全选</el-checkbox>
        <el-checkbox-group v-model="columns" @change="handleCheckedChange">
          <el-checkbox v-for="item in columnList" :label="item.prop" :key="item.prop">
            {{item.label}}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible=false">取 消</el-button>
      <el-button type="primary" @click="downLoad">导 出</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      btnLoading: false,
      type: 0,
      columns: [],
      checkAll: true,
      isIndeterminate: false,
      columnList: []
    }
  },
  methods: {
    init(columnList) {
      this.visible = true
      this.columnList = columnList
      this.columns = columnList.map(o => o.prop)
    },
    handleCheckAllChange(val) {
      this.columns = val ? this.columnList.map(o => o.prop) : [];
      this.isIndeterminate = false;
    },
    handleCheckedChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.columnList.length;
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.columnList.length;
    },
    downLoad() {
      this.$emit('download', { dataType: this.type, selectKey: this.columns.join(',') })
    }
  }
}
</script>
<style lang="scss" scoped>
>>> .el-dialog__body {
  padding: 20px !important;
}
</style>